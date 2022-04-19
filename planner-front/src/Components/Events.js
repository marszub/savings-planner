import Container from "@mui/material/Container";
import Stack from "@mui/material/Stack"
import { useState, useEffect } from "react";
// import { EventData } from "../services/mockData";
import { eventService } from "../services/event-service";
import { EventStorge} from "../services/events-storage"

function getMonth(month){
    let months = [" Jan ", " Feb ", " Mar ", " Apr ", " May ", " Jun ", " Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec "];
    return months[month];
}

function changeTimestamp(EventData) {
    // if (EventData.length > 0) {
    //   console.log("weszlo");
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
    // }
  }

  function sortEvents(events) {
    return events.sort(compare);
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

  function compare(eventA, eventB) {
    let a = new Date(eventA.timestamp);
    let b = new Date(eventB.timestamp);
    if (a.getFullYear() < b.getFullYear()) return -1;
    else if (a.getFullYear() === b.getFullYear())
      if (a.getMonth() < b.getMonth()) return -1;
      else if (a.getMonth() === b.getMonth())
        if (a.getDate() < b.getDate()) return -1;
        else return 1;
  }

export default function Events(){
    const [eventData, setEventData] = useState({});
    const [timestampChanged, setTimestampChanged] = useState(false);

    useEffect(() => {
        function fetchData() {
            console.log(eventData)
          eventService.getEventsList().then((res) => setEventData(res.body.list));
        // setEventData(EventStorge._accessEvent)
          console.log(eventData)

          if (!timestampChanged && Object.keys(eventData).length != 0) {
            console.log("eloo")
            setEventData(changeTimestamp(eventData));

            console.log(eventData[0].timestamp)
            let x = new Date(eventData[0].timestamp);

            if (x instanceof Date && !isNaN(x)) 
                setTimestampChanged(true);
          }
        }
        fetchData()
    })

    const displayStack = () => {
        
        if(Object.keys(eventData).length !== 0){
            console.log("--------")
            console.log(eventData)
            return (
                <Stack>
                {eventData.map((data) => {
                    return (
                    <Container className="event-container">
                        <Stack direction={"row"}>
                            <div className="item">{data.title}</div>
                            <div className="item">{data.timestamp.getDate() +getMonth(data.timestamp.getMonth()) +data.timestamp.getFullYear()}</div>
                            <div className="item">{data.amount} zł</div>
                        </Stack>
                    </Container>
                )})}
            </Stack>
            )
        }
    }

    return(
        <div>
        <Container className="event-container label-row">
        <Stack direction={"row"} style={{fontWeight: "600"}}>
            <div className="item">WYDARZENIE</div>
            <div className="item">DATA</div>
            <div className="item">SALDO</div>
        </Stack>
        </Container>
        <div id="scrollable-container">
            {displayStack()}
        </div>
       </div>
    )
}