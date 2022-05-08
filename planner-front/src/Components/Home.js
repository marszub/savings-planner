import Cashflow from "./Chart/Cashflow";
import Timeline from "./Chart/Timeline";
import Grid from "@mui/material/Grid";
import Events from "./Chart/Events"
import "../styles/Home.css"
import { useState, useEffect } from "react";
import { eventService } from "../services/event-service";
import GoalList from "./Goal/GoalList";
import Header from "./Header";

var initialState = 5000

export default function Home() {
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        function fetchData() {
          eventService.getEventsList().then((res) => setLoading(false))
        }
        fetchData()
    })

    const displayHomePage = () => {
        if(!loading){
            return (
                <>
                    <Header />
                    <div id="main-container">
                        <Grid container spacing={1}>
                            <Grid item xs={9}>
                                <div className="container">
                                    <Cashflow/>

                                </div>
                                <div className="container" style = {{marginTop : '15px'}}>
                                    <Timeline/>
                                </div>
                            </Grid>
                            <Grid item xs={3}>
                                <div className="container">
                                    <h2>Saldo: {initialState}</h2>
                                </div>
                                <div className="container goals">
                                    <GoalList></GoalList>
                                </div>
                            </Grid>
                            <Grid item xs={12}>
                                <div className="container">
                                    {/* <Events/> */}
                                </div>
                            </Grid>
                        </Grid>
                    </div>
                </>
            )
        }
        else
            return (<a>loading data...</a>)
        
    };
    return (
        <div>
            {displayHomePage()} 
        </div>
      )
    }
    
