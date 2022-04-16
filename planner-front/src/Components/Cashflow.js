import React from 'react';
import {Line} from 'react-chartjs-2'
import { useState } from "react";
// import { EventData } from "../services/mockData";
import { eventService } from '../services/event-service';
import { Chart as ChartJS } from "chart.js/auto"

var saldo = 5000
var eventDataSorted =[]
var EventData = eventService.getEventsList()
console.log(EventData)

function sortEvents(events){
    return events.sort(compare);
}

function createCashflow(data){
    var cashData =[]
    for(let i=0; i<data.length;i++){
        if(cashData.length === 0)
          cashData.push(saldo)
        else{
          let current_saldo = cashData[i-1] + data[i].userGain
          cashData.push(current_saldo)
        }
    }
    return cashData;
}

function compare(eventA,eventB){
    let a = new Date(eventA.year);
    let b = new Date(eventB.year);
    if (a.getFullYear() < b.getFullYear())
        return -1
    else if(a.getFullYear() === b.getFullYear())
        if(a.getMonth() < b.getMonth())
            return -1;
        else if (a.getMonth() === b.getMonth())
            if(a.getDate() < b.getDate())
                return -1
    else
    return 1
}
    
function getMonth(month){
    let months = [" Jan ", " Feb ", " Mar ", " Apr ", " May ", " Jun ", " Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec "];
    return months[month];
}

function Cashflow(){
  eventDataSorted = sortEvents(EventData);
  var calculatedCashData = createCashflow(eventDataSorted)

  const [eventData, setEventData] = useState({
    labels: eventDataSorted.map((data) => new Date(data.year).getDate() +  getMonth(new Date(data.year).getMonth()) + new Date(data.year).getFullYear()),
    datasets: [
      {
        data: calculatedCashData.map((data) => data),
        borderColor: '#1A79AD',
        responsive: true,
        borderWidth: 2,
        tension: 0.3,
        easing: 'linear',
        hoverRadius: 10,
        hoverBackgroundColor: '#1A79AD',

      },
    ],})

  const [optionData, setOptionData] = useState({
    responsive: true,
    plugins: {
        legend: {
          display: false
        },
        title: {
            display: false,
            text: "Cashflow",
        },
        tooltip: {
          callbacks: {
              title: function(tooltipItem) {
                let title = eventDataSorted[tooltipItem[0].dataIndex].name
                return title;
              },
              label: function(tooltipItem) {
                let label = eventDataSorted[tooltipItem.dataIndex].userGain + " zł"
                return label;
              }
          }
      }
    }
  })

  return (
    <div id={'chart-wrapper'}>
      <Line data = {eventData} options={optionData}></Line>
    </div> 
  )
}

  export default Cashflow;