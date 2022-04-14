import React, { Component } from 'react';
import {Line} from 'react-chartjs-2'
import { useState } from "react";
import { EventData, Goals } from "../services/mockData";
import { Chart as ChartJS } from "chart.js/auto"

var saldo = 5000
var eventDataSorted =[]


function sortEvents(events){
    return events.sort(compare);
}

function createCashflow(data){
    var cashData =[]
    for(let i of data){
        saldo += i.userGain
        cashData.push(saldo)
    }
    return cashData;
}

function compare(eventA,eventB){
    let a = new Date(eventA.year);
    let b = new Date(eventB.year);
    if (a.getFullYear() < b.getFullYear())
        return -1
    else if(a.getFullYear() == b.getFullYear())
        if(a.getMonth() < b.getMonth())
            return -1;
        else if (a.getMonth() == b.getMonth())
            if(a.getDate() < b.getDate())
                return -1
    else
    return 1
}
    
function getMonth(month){
    let months = [" Jan ", " Feb ", " Mar ", " Apr ", " May ", " Jun ", " Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec "];
    return months[month];
}


const optionData ={
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

  return (
    <div className={'chart-wrapper'}>
      <Line data = {eventData} options={optionData}></Line>
    </div> 
  )
}

  export default Cashflow;