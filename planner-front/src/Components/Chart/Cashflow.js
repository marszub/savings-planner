import React, {useEffect, useState} from "react";
import {Line} from "react-chartjs-2";
import {DateService} from "../../services/date-service";
import {createTheme, ThemeProvider} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import {Container} from "@mui/material";
import {balanceService} from "../../services/balance-service";
import { Chart as ChartJS } from "chart.js/auto"
import { Chart }            from "react-chartjs-2"
import {eventService} from "../../services/event-service";
import {moneyFormatter} from "../../utils/money-formatter";

const theme = createTheme();

export default function Cashflow() {
  const [events, setEvents] = useState([]);

  const [chartData, setChartData] = useState({});
  const [optionData, setOptionData] = useState({});
  const [pluginData, setPluginData] = useState({});

  const [cash, setCash] = useState([]);

  const [balance, setBalance] = useState(0);

  useEffect(() => {
    const changeListener = updatedEvents => setEvents(updatedEvents);
    eventService.addChangeListener(changeListener);

    return () => eventService.removeChangeListener(changeListener);
  }, []);

  useEffect(() => {
    const changeListener = updatedBalance => setBalance(updatedBalance);
    balanceService.addChangeListener(changeListener);

    return () => balanceService.removeChangeListener(changeListener);
  }, []);

  useEffect(() => {
    createCashflow(events, balance);
  }, [events, balance]);

  useEffect(() => {
    updateChart();
  }, [cash]);

  const updateChart = () => {
      setChartData({
        labels: events.map(
            event => {
              const date = new Date(event.timestamp);
              return `${DateService.getMonth(date.getMonth())} ${date.getDate()} ${date.getFullYear()}`
            }
        ),
        datasets: [
          {
            data: cash,
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
                return events[tooltipItem[0].dataIndex].title;
              },
              label: function (tooltipItem) {
                return moneyFormatter.mapPenniesNumberToString(events[tooltipItem.dataIndex].amount) + " PLN";
              },
            },
          },
        },
      });

      setPluginData({
        id: "moveData",

        afterEvent(chart, evt, opts) {
          if (evt.event.type === "click")
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
          drawCircleRight.draw(ctx, right, -5);

          moveChart(chart)
        },
      });
  }

  const createCashflow = (events, balance) => {
    if (events.length === 0) {
      setCash([]);
      return;
    }

    const cashData = [];
    cashData.push((balance + events[0].amount) / 100);

    for (let i = 1; i < events.length; i++) {
      cashData.push(cashData[i - 1] + (events[i].amount / 100));
    }

    setCash(cashData);
  }

  const moveChart = (chart, ex, ey) => {
    const {
      canvas,
      chartArea: { left, right, top, down, width, height }
    } = chart;
    const rect = canvas.getBoundingClientRect();
    const x = ex - rect.left;
    const y = ey - rect.top;

    if (x >= 715 && x <= 745 && y >= 65 && y <= 95) {
      chart.options.scales.x.min = chart.options.scales.x.min + 5;
      chart.options.scales.x.max = chart.options.scales.x.max + 5;
      if (chart.options.scales.x.max >= chart.data.datasets[0].data.length) {
        chart.options.scales.x.max = chart.data.datasets[0].data.length;
        chart.options.scales.x.min = chart.data.datasets[0].data.length - 5;
      }
      chart.update();
    } else if (x >= 0 && x <= 30 && y >= 65 && y <= 95) {
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
