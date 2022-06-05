import * as React from 'react';
import {useEffect, useState} from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import Alert from '@mui/material/Alert';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Snackbar from '@mui/material/Snackbar';
import Tooltip from '@mui/material/Tooltip';
import SportsScoreOutlinedIcon from '@mui/icons-material/SportsScoreOutlined';
import LabelImportantIcon from '@mui/icons-material/LabelImportant';
import CheckIcon from '@mui/icons-material/Check';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import DeleteIcon from '@mui/icons-material/Delete';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import {GoalCreateForm} from '../../models/goal-create-form';
import {goalValidators} from '../../utils/goal-validators';
import {moneyFormatter} from "../../utils/money-formatter";
import {goalService} from "../../services/goal-service";
import {HTTP_CONFLICT, HTTP_CREATED, HTTP_NO_CONTENT, HTTP_NOT_FOUND, HTTP_OK} from "../../utils/http-status";
import {CircularProgress, Collapse, ListItemIcon} from "@mui/material";
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import "../../styles/Goals.css"
import {useNavigate} from "react-router-dom";
import {moneyValidators} from "../../utils/money-validators";
import {SubGoalCreateForm} from "../../models/sub-goal-create-form";
import InputAdornment from "@mui/material/InputAdornment";

const theme = createTheme();

const GOAL_CREATED_ALERT = 'GOAL_CREATED';
const GOAL_DELETED_ALERT = 'GOAL_DELETED';
const GOAL_UPDATED_ALERT = 'GOAL_UPDATED';
const GOAL_404_ALERT = 'GOAL_404';
const GOAL_409_ALERT = 'GOAL_409';
const SUB_GOAL_DELETED_ALERT = 'SUB_GOAL_DELETED';
const SUB_GOAL_CREATED_ALERT = 'SUB_GOAL_CREATED';
const SUB_GOAL_COMPLETED_ALERT = 'SUB_GOAL_COMPLETED';

export default function GoalList() {
  const [goals, setGoals] = useState([]);
  const [goalCreationOpen, setGoalCreationOpen] = useState(false);
  const [alertStatus, setAlertStatus] = useState("");
  const [refreshAlert, setRefreshAlert] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const changeListener = updatedGoals => setGoals(updatedGoals);
    goalService.addChangeListener(changeListener);

    setLoading(true);
    goalService.getList()
        .catch(err => navigate(`/error?text=${err}`))
        .finally(() => setLoading(false));

    return () => goalService.removeChangeListener(changeListener);
  }, []);

  const createGoal = model => {
    setLoading(true);

    goalService.create(model)
        .then(res => {
          switch (res.status) {
            case HTTP_CREATED:
              setAlertStatus(GOAL_CREATED_ALERT);
              setRefreshAlert(prev => !prev);
              break;
            case HTTP_CONFLICT:
              setAlertStatus(GOAL_409_ALERT);
              setRefreshAlert(prev => !prev);
              break;
          }
        })
        .catch(err => navigate(`/error?text=${err}`))
        .finally(() => setLoading(false));
  };

  const deleteGoal = id => {
    setLoading(true);

    goalService.delete(id)
        .then(res => {
          switch (res.status) {
            case HTTP_NO_CONTENT:
              setAlertStatus(GOAL_DELETED_ALERT);
              setRefreshAlert(prev => !prev);
              break;
            case HTTP_NOT_FOUND:
              setAlertStatus(GOAL_404_ALERT);
              setRefreshAlert(prev => !prev);
              break;
          }
        })
        .catch(err => navigate(`/error?text=${err}`))
        .finally(() => setLoading(false));
  };

  const onDragEnd = dnd => {
    if (dnd.source.index === dnd.destination.index) {
      return;
    }

    setLoading(true);

    const sourceGoal = goals[dnd.source.index];
    const destinationGoal = goals[dnd.destination.index];
    const destinationGoalPriority = destinationGoal.priority;

    const newPriorities = new Map();

    if (dnd.source.index < dnd.destination.index) {
      for (let i = dnd.destination.index; i > dnd.source.index; i--) {
        newPriorities.set(goals[i].id, goals[i - 1].priority);
      }
    } else {
      for (let i = dnd.destination.index; i < dnd.source.index; i++) {
        newPriorities.set(goals[i].id, goals[i + 1].priority);
      }
    }
    newPriorities.set(sourceGoal.id, destinationGoalPriority);

    goalService.updatePriority(newPriorities)
        .then(res => {
          switch (res.status) {
            case HTTP_NO_CONTENT:
              setAlertStatus(GOAL_UPDATED_ALERT);
              setRefreshAlert(prev => !prev);
              break;
            case HTTP_NOT_FOUND:
              setAlertStatus(GOAL_404_ALERT);
              setRefreshAlert(prev => !prev);
              break;
            case HTTP_CONFLICT:
              setAlertStatus(GOAL_409_ALERT);
              setRefreshAlert(prev => !prev);
              break;
          }
        })
        .catch(err => navigate(`/error?text=${err}`))
        .finally(() => setLoading(false));
  }

  const goalsItems = goals.map((goal, index) =>
      <Goal
          key={goal.id}
          goal={goal}
          isLast={goal.id === goals[goals.length - 1].id}
          handleDelete={deleteGoal}
          index={index}
          setGoals={setGoals}
          setLoading={setLoading}
          setAlertStatus={setAlertStatus}
          setRefreshAlert={setRefreshAlert}
          navigate={navigate}
      />
  );

  return (
      <ThemeProvider theme={theme}>
        <Container
            component="main"
            maxWidth="xs"
            sx={{
              marginTop: 3,
              position: 'relative',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
            }}
            id="goal-list"
        >
          <CssBaseline/>
          <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
            <SportsScoreOutlinedIcon/>
          </Avatar>

          {loading &&
              <Box sx={{
                backgroundColor: '#fff',
                opacity: 0.9,
                position: 'absolute',
                zIndex: 10,
                width: '100%',
                height: '100%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <CircularProgress/>
              </Box>
          }

          <Typography component="h1" variant="h5">
            Goal list
          </Typography>

          <Container class="scrollable-list-goals" >
          <DragDropContext onDragEnd={onDragEnd}>
            <Droppable droppableId="droppable-list" >
              {provided => (
                  <List
                      sx={{
                        width: '100%',
                        maxWidth: 360,
                        bgcolor: 'background.paper'
                      }}
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                  >
                    {goalsItems}
                    {provided.placeholder}
                  </List>
              )}
            </Droppable>
          </DragDropContext>
          </Container>

          <Button
              fullWidth
              variant="contained"
              sx={{mt: 3, mb: 2}}
              onClick={() => setGoalCreationOpen(true)}
          >
            Add new goal
          </Button>

          <GoalCreationDialog
              open={goalCreationOpen}
              onClose={() => setGoalCreationOpen(false)}
              create={createGoal}
          />

          <GoalActionSnackbar alert={alertStatus} refresh={refreshAlert}/>
        </Container>
      </ThemeProvider>
  );
}

function Goal(props) {
  const [goalRemovalOpen, setGoalRemovalOpen] = useState(false);
  const [subGoalRemovalOpen, setSubGoalRemovalOpen] = useState(false);
  const [subGoalCreationOpen, setSubGoalCreationOpen] = useState(false);
  const [subGoalOpen, setSubGoalOpen] = useState(false);

  const createSubGoal = model => {
    props.setLoading(true);

    goalService.createSubGoal(props.goal.id, model)
        .then(res => {
          switch (res.status) {
            case HTTP_CREATED:
              props.setAlertStatus(SUB_GOAL_CREATED_ALERT);
              props.setRefreshAlert(prev => !prev);
              break;
            case HTTP_NOT_FOUND:
              props.setAlertStatus(GOAL_404_ALERT);
              props.setRefreshAlert(prev => !prev);
              break;
          }
        })
        .catch(err => props.navigate(`/error?text=${err}`))
        .finally(() => props.setLoading(false));
  };

  const completeSubGoal = subGoalId => {
    props.setLoading(true);

    goalService.completeSubGoal(props.goal.id, subGoalId)
        .then(res => {
          switch (res.status) {
            case HTTP_OK:
              props.setAlertStatus(SUB_GOAL_COMPLETED_ALERT);
              props.setRefreshAlert(prev => !prev);
              break;
            case HTTP_NOT_FOUND:
              props.setAlertStatus(GOAL_404_ALERT);
              props.setRefreshAlert(prev => !prev);
              break;
          }
        })
        .catch(err => props.navigate(`/error?text=${err}`))
        .finally(() => props.setLoading(false));
  };

  const deleteSubGoal = subGoalId => {
    props.setLoading(true);

    goalService.deleteSubGoal(props.goal.id, subGoalId)
        .then(res => {
          switch (res.status) {
            case HTTP_OK:
              props.setAlertStatus(SUB_GOAL_DELETED_ALERT);
              props.setRefreshAlert(prev => !prev);
              break;
            case HTTP_NOT_FOUND:
              props.setAlertStatus(GOAL_404_ALERT);
              props.setRefreshAlert(prev => !prev);
              break;
          }
        })
        .catch(err => props.navigate(`/error?text=${err}`))
        .finally(() => props.setLoading(false));
  };

  const subGoals = props.goal.subGoals.map(subGoal => (
      <ListItem
          key={subGoal.id.toString()}
          sx={{
            opacity: subGoal.completed ? 0.5 : 1.0
          }}
      >
        <ListItemIcon
            aria-label="delete"
            size="small"
        >
          <LabelImportantIcon />
        </ListItemIcon>
        <ListItemText
            primary={subGoal.title}
            secondary={moneyFormatter.mapPenniesNumberToString(subGoal.amount) + ' PLN'}
        />
        { !subGoal.completed &&
          <Tooltip title="Complete">
            <IconButton
                edge="end"
                aria-label="complete"
                size="small"
                onClick={() => completeSubGoal(subGoal.id)}
            >
              <CheckIcon/>
            </IconButton>
          </Tooltip>
        }
        <Tooltip title="Delete">
          <IconButton
              edge="end"
              aria-label="delete"
              size="small"
              onClick={() => setSubGoalRemovalOpen(true)}
          >
            <DeleteIcon />
          </IconButton>
        </Tooltip>
        <RemovalConfirmationDialog
            open={subGoalRemovalOpen}
            onClose={() => setSubGoalRemovalOpen(false)}
            delete={() => deleteSubGoal(subGoal.id)}
            title={subGoal.title}
        />
      </ListItem>
  ));

  return (
      <>
        <Draggable key={props.goal.id.toString()} draggableId={props.goal.id.toString()} index={props.index}>
          {(provided) => (
              <>
              <ListItem
                  ref={provided.innerRef}
                  {...provided.draggableProps}
                  {...provided.dragHandleProps}
              >
                <Tooltip title={subGoalOpen ? 'Collapse' : 'Expand'}>
                  <IconButton
                      edge="start"
                      aria-label="expand"
                      onClick={() => setSubGoalOpen(prev => !prev)}
                  >
                    {subGoalOpen ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                  </IconButton>
                </Tooltip>
                <ListItemText
                    primary={props.goal.title}
                    secondary={moneyFormatter.mapPenniesNumberToString(props.goal.amount) + ' PLN'}
                />
                <Tooltip title="Delete">
                  <IconButton
                      edge="end"
                      aria-label="delete"
                      onClick={() => setGoalRemovalOpen(true)}
                  >
                    <DeleteIcon/>
                  </IconButton>
                </Tooltip>
                <RemovalConfirmationDialog
                    open={goalRemovalOpen}
                    onClose={() => setGoalRemovalOpen(false)}
                    delete={() => props.handleDelete(props.goal.id)}
                    title={props.goal.title}
                />
              </ListItem>
              <Collapse in={subGoalOpen} timeout="auto" unmountOnExit>
                <List>
                  {subGoals}
                </List>

                <Button
                    fullWidth
                    variant="contained"
                    sx={{mt: 3, mb: 2}}
                    onClick={() => setSubGoalCreationOpen(true)}
                >
                  Add new sub-goal
                </Button>

                <SubGoalCreationDialog
                    open={subGoalCreationOpen}
                    onClose={() => setSubGoalCreationOpen(false)}
                    create={createSubGoal}
                />
              </Collapse>
              </>
          )}
        </Draggable>

        {!props.isLast &&
            <Divider/>
        }
      </>
  );
}

function GoalCreationDialog(props) {
  const [titleErrorMessage, setTitleErrorMessage] = useState("");

  const handleClose = () => {
    props.onClose();
    setTitleErrorMessage('');
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const data = new FormData(event.currentTarget);
    const formModel = new GoalCreateForm(
        data.get('title'),
    );

    const titleError = goalValidators.validateTitle(formModel.title);

    setTitleErrorMessage(titleError);

    if (titleError) {
      return;
    }

    props.create(formModel);
    handleClose();
  };

  return (
      <Dialog
          maxWidth="xs"
          open={props.open}
          onClose={handleClose}
      >
        <Box
            sx={{
              margin: 1
            }}
        >
          <DialogTitle>Create new goal</DialogTitle>
          <Box
              component="form"
              onSubmit={handleSubmit}
          >
            <DialogContent>
              <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="title"
                  label="Title"
                  name="title"
                  autoFocus
                  error={!!titleErrorMessage}
                  helperText={titleErrorMessage}
              />
            </DialogContent>
            <DialogActions>
              <Button
                  type="button"
                  onClick={handleClose}
              >
                Cancel
              </Button>
              <Button
                  type="submit"
                  variant="contained"
              >
                Create
              </Button>
            </DialogActions>
          </Box>
        </Box>
      </Dialog>
  );
}

function SubGoalCreationDialog(props) {
  const [titleErrorMessage, setTitleErrorMessage] = useState("");
  const [amountErrorMessage, setAmountErrorMessage] = useState("");

  const handleClose = () => {
    props.onClose();
    setTitleErrorMessage('');
    setAmountErrorMessage('');
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const data = new FormData(event.currentTarget);
    const formModel = new SubGoalCreateForm(
        data.get('title'),
        data.get('amount')
    );

    const titleError = goalValidators.validateTitle(formModel.title);
    const amountError = moneyValidators.validateAmount(formModel.amount);

    setTitleErrorMessage(titleError);
    setAmountErrorMessage(amountError);

    if (titleError || amountError) {
      return;
    }

    props.create(formModel);
    handleClose();
  };

  return (
      <Dialog
          maxWidth="xs"
          open={props.open}
          onClose={handleClose}
      >
        <Box
            sx={{
              margin: 1
            }}
        >
          <DialogTitle>Create new sub-goal</DialogTitle>
          <Box
              component="form"
              onSubmit={handleSubmit}
          >
            <DialogContent>
              <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="title"
                  label="Title"
                  name="title"
                  autoFocus
                  error={!!titleErrorMessage}
                  helperText={titleErrorMessage}
              />
              <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="amount"
                  label="Amount"
                  name="amount"
                  error={!!amountErrorMessage}
                  helperText={amountErrorMessage}
                  InputProps={{
                    endAdornment: <InputAdornment position="end">PLN</InputAdornment>,
                  }}
              />
            </DialogContent>
            <DialogActions>
              <Button
                  type="button"
                  onClick={handleClose}
              >
                Cancel
              </Button>
              <Button
                  type="submit"
                  variant="contained"
              >
                Create
              </Button>
            </DialogActions>
          </Box>
        </Box>
      </Dialog>
  );
}

function RemovalConfirmationDialog(props) {
  const handleDelete = () => {
    props.delete();
    props.onClose();
  }

  return (
      <Dialog
          maxWidth="xs"
          open={props.open}
          onClose={props.onClose}
      >
        <Box
            sx={{
              margin: 1
            }}
        >
          <DialogTitle>Confirmation</DialogTitle>
          <DialogContent>
            This action cannot be undone. Are you sure you want to delete <strong>{props.title}</strong>?
          </DialogContent>
          <DialogActions>
            <Button
                type="button"
                onClick={props.onClose}
            >
              Cancel
            </Button>
            <Button
                type="button"
                variant="contained"
                color="warning"
                onClick={handleDelete}
            >
              Delete
            </Button>
          </DialogActions>
        </Box>
      </Dialog>
  )
}

function GoalActionSnackbar(props) {
  const [alertOpen, setAlertOpen] = useState(false);
  const [alertSeverity, setAlertSeverity] = useState('info');
  const [alertMessage, setAlertMessage] = useState('');

  const updateAlert = () => {
    switch (props.alert) {
      case '':
        setAlertOpen(false);
        break;
      case GOAL_CREATED_ALERT:
        setAlertSeverity('success');
        setAlertMessage('New goal successfully created!');
        setAlertOpen(true);
        break;
      case GOAL_DELETED_ALERT:
        setAlertSeverity('info');
        setAlertMessage('Goal successfully deleted!');
        setAlertOpen(true);
        break;
      case SUB_GOAL_CREATED_ALERT:
        setAlertSeverity('success');
        setAlertMessage('New sub-goal successfully created!');
        setAlertOpen(true);
        break;
      case SUB_GOAL_COMPLETED_ALERT:
        setAlertSeverity('info');
        setAlertMessage('Sub-goal completed!');
        setAlertOpen(true);
        break;
      case SUB_GOAL_DELETED_ALERT:
        setAlertSeverity('info');
        setAlertMessage('Sub-goal successfully deleted!');
        setAlertOpen(true);
        break;
      case GOAL_UPDATED_ALERT:
        setAlertSeverity('info');
        setAlertMessage('Goals successfully updated!');
        setAlertOpen(true);
        break;
      case GOAL_404_ALERT:
        setAlertSeverity("error");
        setAlertMessage("This goal does not exist!");
        setAlertOpen(true);
        break;
      case GOAL_409_ALERT:
        setAlertSeverity("error");
        setAlertMessage("Goal conflict!");
        setAlertOpen(true);
        break;
      default:
        console.log('Unknown alert status ' + props.alert);
        break;
    }
  };

  const handleAlertClose = () => {
    setAlertOpen(false);
  }

  useEffect(() => {
      updateAlert();
  }, [props.refresh]);

  return (
      <Snackbar
          open={alertOpen}
          autoHideDuration={5000}
          onClose={handleAlertClose}
      >
        <Alert
            severity={alertSeverity}
            variant="filled"
            onClose={handleAlertClose}
        >
          {alertMessage}
        </Alert>
      </Snackbar>
  );
}
