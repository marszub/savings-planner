import React from "react";
import { Line } from "react-chartjs-2";
import { useState, useEffect } from "react";
import { EventStorge } from "../../services/events-storage";
import { DateService } from "../../services/date-service";

var saldo = 5000;

function createCashflow(data) {
  var cashData = [];
  for (let i = 0; i < data.length; i++) {
    if (cashData.length === 0) cashData.push(saldo + data[i].amount);
    else {
      let current_saldo = cashData[i - 1] + data[i].amount;
      cashData.push(current_saldo);
    }
  }
  console.log(cashData);
  return cashData;
}

export default function Cashflow() {
  const [chartData, setChartData] = useState({});
  const [optionData, setOptionData] = useState({});

  const [eventData, setEventData] = useState({});
  const [timestampChanged, setTimestampChanged] = useState(false);
  const [cash, setCash] = useState([]);

  useEffect(() => {
    function fetchData() {
      if(EventStorge.accessEvents.length>0){
        setEventData(EventStorge.accessEvents)
      }

      if (!timestampChanged && Object.keys(eventData).length !== 0) {
        setCash(createCashflow(eventData));

        setChartData({
          labels: eventData.map(
            (data) =>
              data.timestamp.getDate()
                + " " + DateService.getMonth(data.timestamp.getMonth()) + " "
                + data.timestamp.getFullYear()
          ),
          datasets: [
            {
              data: cash.map((data) => data),
              borderColor: "#1A79AD",
              responsive: true,
              borderWidth: 2,
              tension: 0.3,
              easing: "linear",
              hoverRadius: 10,
              hoverBackgroundColor: "#1A79AD",
            },
          ],
        });

        setOptionData({
          responsive: true,
          plugins: {
            legend: {
              display: false,
            },
            title: {
              display: false,
              text: "Cashflow",
            },
            tooltip: {
              callbacks: {
                title: function (tooltipItem) {
                  let title = eventData[tooltipItem[0].dataIndex].title;
                  return title;
                },
                label: function (tooltipItem) {
                  let label = eventData[tooltipItem.dataIndex].amount + " zł";
                  return label;
                },
              },
            },
          },
        });

        if (
          cash.length !== 0 &&
          Object.keys(chartData).length !== 0 &&
          Object.keys(optionData).length !== 0
        ) {
          console.log(cash);
          setTimestampChanged(true);
        }
      }
    }

    fetchData();
  });

  const renderChart = () => {
    if (
      Object.keys(chartData).length !== 0 &&
      Object.keys(optionData).length !== 0
    )
      return <Line data={chartData} options={optionData}></Line>;
  };

  return <div className={"chart-wrapper"}>{renderChart()}</div>;
}
