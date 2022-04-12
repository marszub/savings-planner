import * as React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import AccordionDetails from "@mui/material/AccordionDetails";
import {Divider, List, ListItem, ListItemText} from "@mui/material";
import Box from "@mui/material/Box";

const theme = createTheme();

export default function GoalList() {
  const goals = [
    {
      id: 1,
      title: 'Goal Name 1',
      amount: 1000
    },
    {
      id: 2,
      title: 'Goal Name 2',
      amount: 2000
    },
    {
      id: 3,
      title: 'Goal Name 3',
      amount: 3000
    },
    {
      id: 4,
      title: 'Goal Name 4',
      amount: 4000
    },
    {
      id: 5,
      title: 'Goal Name 5',
      amount: 5000
    }
  ]

  const goalsItems = goals.map(goal =>
    <Goal goal={goal}></Goal>
  );

  return (
      <ThemeProvider theme={theme}>
        <Container component="main">
          <CssBaseline />
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <Typography component="h1" variant="h5">
              Goal list
            </Typography>
          </Box>
          {goalsItems}
        </Container>
      </ThemeProvider>
  );
}

function Goal(props) {
  return (
      <List sx={{ bgcolor: 'secondary.main' }}>
        <ListItem key={props.goal.id}>
          <ListItemText
              primary={props.goal.title}
              secondary={props.goal.amount + ' PLN'}
          />
        </ListItem>
      </List>
  )
}
