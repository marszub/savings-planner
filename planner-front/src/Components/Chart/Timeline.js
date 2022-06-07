import React, { useEffect, useState } from "react";
import HorizontalTimeline from "react-horizontal-timeline";
import Typography from "@mui/material/Typography";
import { Container } from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { goalService } from "../../services/goal-service";
import { goalCalculateDates } from "../../utils/goal-calculate-dates";
import {eventService} from "../../services/event-service";
import {balanceService} from "../../services/balance-service";

const theme = createTheme();

export default function Timeline() {
  const [curIdx, setCurIdx] = useState(0);
  const [goals, setGoals] = useState([]);
  const [events, setEvents] = useState([]);
  const [balance, setBalance] = useState(0);

  useEffect(() => {
    const changeListener = updatedGoals => setGoals(updatedGoals);
    goalService.addChangeListener(changeListener);

    return () => goalService.removeChangeListener(changeListener);
  }, []);

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

  const getIndex = index => {
    if (goals.length === 0) {
      return 0;
    } else if (index < goals.length) {
      return index;
    } else {
      setCurIdx(goals.length - 1);
      return goals.length - 1;
    }
  }

  const curStatus = goals[getIndex(curIdx)]?.title;

  return (
      <ThemeProvider theme={theme}>
        <Container>
          <Typography
              component="h1"
              variant="h5"
              style={{ textAlign: "center" }}
          >
              { `Goal progress: ${ curStatus ? curStatus : 'no goals' }` }
          </Typography>
          <div
              style={{
                height: "100px",
                marginTop: "35px",
                fontSize: "13px",
              }}
          >
              { goals.length > 0 && <HorizontalTimeline
                styles={{
                  background: "#ffffff",
                  foreground: "#1976d2",
                  outline: "#dfdfdf",
                }}
                index={getIndex(curIdx)}
                indexClick={i => setCurIdx(i)}
                values={ goalCalculateDates(goals, balance, events) }
            /> }
          </div>
        </Container>
      </ThemeProvider>
  );
}
