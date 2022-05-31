import React, { useEffect, useState } from "react";
import HorizontalTimeline from "react-horizontal-timeline";
import Typography from "@mui/material/Typography";
import { Container } from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { goalService } from "../../services/goal-service";

const theme = createTheme();

function compareDates(event1, event2) {
  if (event1.date < event2.date) return -1;
  else return 1;
}

export default function Timeline() {
  const [curIdx, setCurIdx] = useState(0);
  const [prevIdx, setPrevIdx] = useState(-1);
  const [goals, setGoals] = useState([]);

  useEffect(() => {
    const changeListener = updatedGoals => setGoals(updatedGoals);
    goalService.addChangeListener(changeListener);

    return () => goalService.removeChangeListener(changeListener);
  }, []);

  goals.sort(compareDates);
  const curStatus = goals[curIdx]?.title;

  return (
      <ThemeProvider theme={theme}>
        <Container>
          <Typography
              component="h1"
              variant="h5"
              style={{ textAlign: "center" }}
          >
              { curStatus ? `Goal progress: ${curStatus}`: 'Goal progress: no goals' }
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
                index={curIdx}
                indexClick={(index) => {
                  setPrevIdx(curIdx);
                  setCurIdx(index);
                }}
                values={ goals.map(goal => new Date()) }
            /> }
          </div>
        </Container>
      </ThemeProvider>
  );
}
