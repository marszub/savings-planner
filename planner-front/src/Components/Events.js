import Container from "@mui/material/Container";
import Stack from "@mui/material/Stack"
import { useState, useEffect } from "react";
import { EventStorge} from "../services/events-storage"
import { DateService} from "../services/date-service"

export default function Events(){
    const [eventData, setEventData] = useState([]);

    useEffect(() => {
        function fetchData() {
        if(EventStorge.accessEvents.length>0){
            setEventData(EventStorge.accessEvents)
        }
        }
        fetchData()
    })

    const displayStack = () => {
        if(eventData.length !== 0){
            console.log(eventData)
            return (
                <Stack>
                {eventData.map((data) => {
                    return (
                    <Container className="event-container" key={data.timestamp}>
                        <Stack direction={"row"}>
                            <div className="item">{data.title}</div>
                            <div className="item">{data.timestamp.getDate() + DateService.getMonth(data.timestamp.getMonth()) +data.timestamp.getFullYear()}</div>
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