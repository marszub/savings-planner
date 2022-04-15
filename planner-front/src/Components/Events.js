import Container from "@mui/material/Container";
import Stack from "@mui/material/Stack"
import { style } from "@mui/system";
import { useState } from "react";
import { EventData } from "../services/mockData";

function getMonth(month){
    let months = [" Jan ", " Feb ", " Mar ", " Apr ", " May ", " Jun ", " Jul ", " Aug ", " Sep ", " Oct ", " Nov ", " Dec "];
    return months[month];
}

export default function Events(){
    const [eventData, setEventData] = useState(
        EventData
    )
    return(
        <div>
        <Container class="event-container label-row">
        <Stack direction={"row"} style={{fontWeight: "600"}}>
            <div class="item">WYDARZENIE</div>
            <div class="item">DATA</div>
            <div class="item">SALDO</div>
        </Stack>
        </Container>
        <div id="scrollable-container">
         <Stack>
            {eventData.map((data) => {
                return (
                <Container class="event-container">
                    <Stack direction={"row"}>
                        <div class="item">{data.name}</div>
                        <div class="item">{new Date(data.year).getDate() + getMonth(new Date(data.year).getMonth()) + new Date(data.year).getFullYear()}</div>
                        <div class="item">{data.userGain} zł</div>
                    </Stack>
                </Container>
            )})}
        </Stack>
        </div>
       </div>
    )
}