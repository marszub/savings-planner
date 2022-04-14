import Cashflow from "./Cashflow";
import Timeline from "./Timeline";
import Grid from "@mui/material/Grid";
import Events from "./Events"
import Goals from "./Goals"
import "../styles/Home.css"

var initialState = 5000

export default function Home() {
    return (
        <div id="main-container">
        <Grid container spacing={2}>
            <Grid item xs={9}>
                <div class="container">
                    <div class="title">
                        <a>Cash Flow</a>
                    </div>
                    <Cashflow/>
                </div>
            </Grid>
            <Grid item xs={3}>
                <div class="container">
                    <h2>Saldo: {initialState}</h2>
                </div>
                <div class="container goals">
                    <Goals></Goals>
                </div>
            </Grid>
            <Grid item xs={9}>
                <div class="container">
                    <Timeline/>
                </div>
            </Grid>
            
            <Grid item xs={12}>
                <div class="container">
                    <Events/>
                </div>  
            </Grid>
        </Grid>
        </div>
      );
    }
    
