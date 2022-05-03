import React from "react";
import { Line } from "react-chartjs-2";
import { useState, useEffect } from "react";
import { EventStorge } from "../../services/events-storage";
import { DateService } from "../../services/date-service";
import { Chart as ChartJS } from 'chart.js/auto'
import { Chart }            from 'react-chartjs-2'
import { createTheme, ThemeProvider } from "@mui/material/styles";
import  Typography from '@mui/material/Typography';
import { Container } from "@mui/material";

var saldo = 5000;
const theme = createTheme();

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

function moveChart(chart){ 
  const {ctx, canvas, chartArea: {left, right, top, down, width, height}} = chart;
  
  canvas.addEventListener('click', (e) => {
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    if(x>= right -15 && x<= right+15 && y >= height /2 +top -15 && y<= height /2 +top +15){
      console.log("right")
      chart.options.scales.x.min = chart.options.scales.x.min +5
      chart.options.scales.x.max = chart.options.scales.x.max +5
      if(chart.options.scales.x.max >= chart.data.datasets[0].data.length){
        chart.options.scales.x.max = chart.data.datasets[0].data.length;
        chart.options.scales.x.min = chart.data.datasets[0].data.length -5
      }
      chart.update()
    }

    else if(x>= left -15 && x<= left+15 && y >= height /2 +top -15 && y<= height /2 +top +15){
          console.log("left")
          chart.options.scales.x.min = chart.options.scales.x.min -5
          chart.options.scales.x.max = chart.options.scales.x.max -5
          if(chart.options.scales.x.min <= 0){
            chart.options.scales.x.max = 5
            chart.options.scales.x.min = 0
          }
          chart.update()
        }
  })
}

export default function Cashflow() {
  const [chartData, setChartData] = useState({});
  const [optionData, setOptionData] = useState({});
  const [pluginData, setPluginData] = useState({});

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
              borderColor: "#1c54b2",
              responsive: true,
              borderWidth: 2,
              tension: 0.3,
              easing: "linear",
              hoverRadius: 10,
              hoverBackgroundColor: "#1c54b2",
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
            y:{
              beginAtZero: false,
            }
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

        setPluginData({
          id: 'moveData',

          afterEvent(chart, args){
            moveChart(chart)
          },

          afterDraw(chart, args, pluginOptions){
            const {ctx, chartArea: {left, right, top, bottom, width, height}} = chart;

            class CircleChevron{
              draw(ctx, x1, pixel){
                const angle = Math.PI /180;

                ctx.beginPath();
                ctx.lineWidth = 2;
                ctx.strokeStyle = '#1c54b2';
                ctx.fillStyle= 'rgba(255,255,255,0.5)'
                ctx.arc(x1, height /2 + top, 15, angle*0, angle * 360, false) 
                ctx.stroke()
                ctx.fill()
                ctx.closePath()
                
    
                ctx.beginPath();
                ctx.lineWidth = 2;
                ctx.strokeStyle = '#1c54b2';
                ctx.moveTo(x1 +pixel, height / 2 + top - 7.5)
                ctx.lineTo(x1 -pixel, height / 2 + top)
                ctx.lineTo(x1 +pixel, height / 2 + top + 7.5)
                ctx.stroke()
                ctx.closePath()
              }
            }
           
            let drawCircleLeft = new CircleChevron();
            drawCircleLeft.draw(ctx,left, 5)

            let drawCircleRight = new CircleChevron();
            drawCircleLeft.draw(ctx,right, -5)
          }
        })

        if (
          cash.length !== 0 &&
          Object.keys(chartData).length !== 0 &&
          Object.keys(optionData).length !== 0 &&
          Object.keys(pluginData.length !== 0)
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
      return (
        <ThemeProvider theme={theme}>
          <Container>
            <Typography component="h1" variant="h5" style={{textAlign: "center"}}>
              Cash Flow
            </Typography>
            <Line data={chartData} options={optionData} plugins={[pluginData]} ></Line>
          </Container>
        </ThemeProvider>
      ) 
  };

  return <div className={"chart-wrapper"}>{renderChart()}</div>;
}
