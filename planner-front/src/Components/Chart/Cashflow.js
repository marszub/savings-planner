import React, {useEffect, useState} from "react";
import {Line} from "react-chartjs-2";
import {EventStorge} from "../../services/events-storage";
import {DateService} from "../../services/date-service";
import {createTheme, ThemeProvider} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import {Container} from "@mui/material";
import {balanceService} from "../../services/balance-service";

const theme = createTheme();

export default function Cashflow() {
  const [chartData, setChartData] = useState({});
  const [optionData, setOptionData] = useState({});
  const [pluginData, setPluginData] = useState({});

  const [eventData, setEventData] = useState({});
  const [cash, setCash] = useState([]);

  const [balance, setBalance] = useState();

  const fetchData = () => {
    if (EventStorge.accessEvents.length > 0) {
      setEventData(EventStorge.accessEvents);
    }

    if (Object.keys(eventData).length !== 0) {
      setCash(createCashflow(eventData, balance));

      setChartData({
        labels: eventData.map(
            (data) =>
                DateService.getMonth(data.timestamp.getMonth()) +
                " " +
                data.timestamp.getDate() +
                " " +
                data.timestamp.getFullYear()
        ),
        datasets: [
          {
            data: cash.map(data => data),
            borderColor: "#1976d2",
            responsive: true,
            borderWidth: 2,
            tension: 0.3,
            easing: "linear",
            hoverRadius: 10,
            hoverBackgroundColor: "#1976d2",
          },
        ],
      });

      setOptionData({
        responsive: true,
        scales: {
          x: {
            min: 0,
            max: 4,
          },
          y: {
            beginAtZero: false,
          },
        },
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
                return eventData[tooltipItem[0].dataIndex].title;
              },
              label: function (tooltipItem) {
                return eventData[tooltipItem.dataIndex].amount + " PLN";
              },
            },
          },
        },
      });

      setPluginData({
        id: "moveData",

        afterEvent(chart, evt, opts) {
          if (evt.event.type == "click")
            moveChart(chart, evt.event.x, evt.event.y);
        },

        afterDraw(chart, args, pluginOptions) {
          const {
            ctx,
            chartArea: { left, right, top, bottom, width, height },
          } = chart;

          class CircleChevron {
            draw(ctx, x1, pixel) {
              const angle = Math.PI / 180;

              ctx.beginPath();
              ctx.lineWidth = 2;
              ctx.strokeStyle = "#1c54b2";
              ctx.fillStyle = "rgba(255, 255, 255, 0.5)";
              ctx.arc(
                  x1,
                  height / 2 + top,
                  15,
                  0,
                  angle * 360,
                  false
              );
              ctx.stroke();
              ctx.fill();
              ctx.closePath();

              ctx.beginPath();
              ctx.lineWidth = 2;
              ctx.strokeStyle = "#1c54b2";
              ctx.moveTo(x1 + pixel, height / 2 + top - 7.5);
              ctx.lineTo(x1 - pixel, height / 2 + top);
              ctx.lineTo(x1 + pixel, height / 2 + top + 7.5);
              ctx.stroke();
              ctx.closePath();
            }
          }

          const drawCircleLeft = new CircleChevron();
          drawCircleLeft.draw(ctx, left, 5);

          const drawCircleRight = new CircleChevron();
          drawCircleLeft.draw(ctx, right, -5);

          // moveChart(chart)
        },
      });

      if (
          cash.length !== 0 &&
          Object.keys(chartData).length !== 0 &&
          Object.keys(optionData).length !== 0 &&
          Object.keys(pluginData.length !== 0)
      ) {
        console.log(cash);
      }
    }
  }

  useEffect(() => {
    const changeListener = updatedBalance => setBalance(updatedBalance);
    balanceService.addChangeListener(changeListener);

    return () => balanceService.removeChangeListener(changeListener);
  }, []);

  useEffect(() => {
    console.log('cashflow use effect 2');

    fetchData();

  }, [balance]);

  const createCashflow = (data, balance) => {
    console.log('createCashflow');
    const cashData = [];
    for (let i = 0; i < data.length; i++) {
      if (cashData.length === 0) {
        cashData.push(balance + data[i].amount);
      } else {
        let currentBalance = cashData[i - 1] + data[i].amount;
        cashData.push(currentBalance);
      }
    }
    console.log(cashData);
    return cashData;
  }

  const moveChart = (chart, ex, ey) => {
    const {
      canvas,
      chartArea: { left, right, top, down, width, height }
    } = chart;
    const rect = canvas.getBoundingClientRect();
    const x = ex - rect.left;
    const y = ey - rect.top;

    console.log(rect);
    console.log(x, y);
    console.log(right, left);

    if (x >= 715 && x <= 745 && y >= 65 && y <= 95) {
      console.log("right");
      chart.options.scales.x.min = chart.options.scales.x.min + 5;
      chart.options.scales.x.max = chart.options.scales.x.max + 5;
      if (chart.options.scales.x.max >= chart.data.datasets[0].data.length) {
        chart.options.scales.x.max = chart.data.datasets[0].data.length;
        chart.options.scales.x.min = chart.data.datasets[0].data.length - 5;
      }
      chart.update();
    } else if (x >= 0 && x <= 30 && y >= 65 && y <= 95) {
      console.log("left");
      chart.options.scales.x.min = chart.options.scales.x.min - 5;
      chart.options.scales.x.max = chart.options.scales.x.max - 5;
      if (chart.options.scales.x.min <= 0) {
        chart.options.scales.x.max = 5;
        chart.options.scales.x.min = 0;
      }
      chart.update();
    }
  }

  return (
      <div className={"chart-wrapper"}>
        { (Object.keys(chartData).length && Object.keys(optionData).length) &&
            <ThemeProvider theme={theme}>
              <Container>
                <Typography
                    component="h1"
                    variant="h5"
                    style={{textAlign: "center"}}
                >
                  Cash Flow
                </Typography>
                <Line
                    data={chartData}
                    options={optionData}
                    plugins={[pluginData]}
                />
              </Container>
            </ThemeProvider>
        }
      </div>
  );
}
