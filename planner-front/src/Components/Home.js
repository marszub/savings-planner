import Cashflow from "./Chart/Cashflow";
import Timeline from "./Chart/Timeline";
import Grid from "@mui/material/Grid";
import "../styles/Home.css";
import { useState, useEffect } from "react";
import { eventService } from "../services/event-service";
import GoalList from "./Goal/GoalList";
import EventList from "./Event/EventList";
import BalanceField from "./Balance/BalanceField";
import Header from "./Header";

export default function Home() {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    function fetchData() {
      eventService.getEventsList().then((res) => setLoading(false));
    }
    fetchData();
  });

  const displayHomePage = () => {
    if (!loading) {
      return (
        <body>
          <Header />
          <div id="main-container">
            <Grid container spacing={2} id="main-grid">
              <Grid item xs={9}>
                <div className="container" style={{height:"40vw"}}>
                  <Cashflow />
                </div>
              </Grid>
              <Grid item xs={3}>
                <div className="container"style={{height:"15vw"}}>
                  <BalanceField></BalanceField>
                </div>
                <div className="container" style={{marginTop:"15px"}}>
                  <a>cashflow options</a>
                </div>
              </Grid>
              <Grid item xs={8}>
                <div className="container ">
                  <EventList></EventList>
                </div>
              </Grid>
              <Grid item xs={4}>
                <div className="container">
                  <GoalList></GoalList>
                </div>
              </Grid>
              <Grid item xs={12}>
                <div className="container">
                  <Timeline></Timeline>
                </div>
              </Grid>
            </Grid>
          </div>
        </body>
      );
    } else return <a>loading data...</a>;
  };
  return <div>{displayHomePage()}</div>;
}
