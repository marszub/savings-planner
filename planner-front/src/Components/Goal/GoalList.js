import * as React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { Divider, List, ListItem, ListItemText } from "@mui/material";
import Box from "@mui/material/Box";

const theme = createTheme();

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

export default function GoalList() {
  const goalsItems = goals.map(goal =>
    <Goal goal={goal} />
  );

  return (
      <ThemeProvider theme={theme}>
        <Container component="main" maxWidth="xs" sx={{ marginTop: 8, border: 1, borderColor: 'text.secondary', borderRadius: 3 }} >
          <CssBaseline />
          <Box sx={{ paddingTop: 2, display: 'flex', flexDirection: 'column', alignItems: 'center' }} >
            <Typography component="h1" variant="h5">
              Goal list
            </Typography>
          </Box>
          <List sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }} >
              { goalsItems }
          </List>
        </Container>
      </ThemeProvider>
  );
}

function Goal(props) {
  return (
      <>
          <ListItem key={ props.goal.id }>
              <ListItemText
                  primary={ props.goal.title }
                  secondary={ props.goal.amount + ' PLN' }
              />
          </ListItem>
          { props.goal.id !== goals[goals.length-1].id &&
            <Divider light />
          }
      </>
  )
}
