import React from "react";
import { Line } from "react-chartjs-2";
import { useState, useEffect } from "react";
import { eventService } from "../services/event-service";
import { Chart as ChartJS } from "chart.js/auto";

var saldo = 5000;

function changeTimestamp(EventData) {
  if (EventData.length > 0) {
    console.log("weszlo");
    console.log(EventData);
    for (let event of EventData) {
      let x = new Date(event.timestamp);
      if (x instanceof Date && !isNaN(x)) {
        continue;
      } else {
        event.timestamp = parseDate(event.timestamp);
        event.amount /= 100;
      }
    }
    EventData = sortEvents(EventData);
    // console.log(EventData)
    return EventData;
  }
}

function sortEvents(events) {
  return events.sort(compare);
}

function createCashflow(data) {
  // eventService.getEventsList()
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

function compare(eventA, eventB) {
  let a = new Date(eventA.timestamp);
  let b = new Date(eventB.timestamp);
  if(a < b)
    return -1
  else
    return 1
}

function getMonth(month) {
  let months = [
    " Jan ",
    " Feb ",
    " Mar ",
    " Apr ",
    " May ",
    " Jun ",
    " Jul ",
    " Aug ",
    " Sep ",
    " Oct ",
    " Nov ",
    " Dec ",
  ];
  return months[month];
}

function parseDate(time) {
  var timeArr = time.split(" ");
  var date = timeArr[0].split("-");
  var hour = timeArr[1].split(":");
  var jsDate = new Date(
    date[0],
    parseInt(date[1]) - 1,
    date[2],
    hour[0],
    hour[1],
    hour[2]
  );
  return jsDate;
}

export default function Cashflow() {
  const [chartData, setChartData] = useState({});
  const [optionData, setOptionData] = useState({});

  const [eventData, setEventData] = useState({});
  const [timestampChanged, setTimestampChanged] = useState(false);
  const [cash, setCash] = useState([]);

  useEffect(() => {
    function fetchData() {
      eventService.getEventsList().then((res) => setEventData(res.body.list));

      if (!timestampChanged && Object.keys(eventData).length != 0) {
        // console.log(Object.keys(eventData).length === 0);
        setEventData(changeTimestamp(eventData));

        setCash(createCashflow(eventData));

        setChartData({
          labels: eventData.map(
            (data) =>
              data.timestamp.getDate() +
              getMonth(data.timestamp.getMonth()) +
              data.timestamp.getFullYear()
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
          cash.length != 0 &&
          Object.keys(chartData).length != 0 &&
          Object.keys(optionData).length != 0
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
      Object.keys(chartData).length != 0 &&
      Object.keys(optionData).length != 0
    )
      return <Line data={chartData} options={optionData}></Line>;
  };

  return <div className={"chart-wrapper"}>{renderChart()}</div>;
}
