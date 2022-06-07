import { httpService } from "./http-service";
import {HTTP_CREATED, HTTP_NO_CONTENT, HTTP_NOT_FOUND, HTTP_OK} from "../utils/http-status";
import {
  CreateCyclicEventRequest,
  CreateEventRequest,
  CreateOneTimeEventRequest
} from "../requests/create-event-request";
import { moneyFormatter } from "../utils/money-formatter";
import { INCOME_EVENT_TYPE } from "../utils/event-types";
import { UpdateEventRequest } from "../requests/update-event-request";

function compare(eventA, eventB) {
  return eventA.timestamp - eventB.timestamp;
}

export const eventService = {

    _events: [],
    _changeListeners: [],

    addChangeListener(onChange) {
      this._changeListeners.push(onChange);
      onChange([...this._events]);
    },

    removeChangeListener(onChange) {
      this._changeListeners = this._changeListeners.filter(listener => listener !== onChange);
    },

    _notifyChangeListeners() {
      this._changeListeners.forEach(onChange => onChange([...this._events]));
    },

    getList() {
        return httpService.get("/events")
            .then(res => {
              switch (res.status) {
                case HTTP_OK:
                  this._events = res.body.events;
                  this._events.sort(compare);
                  this._notifyChangeListeners();
                  break;
                default:
                  return httpService.onUnexpectedHttpStatus(res.status);
              }
              return res;
            });
    },

    create(cyclic, formModel) {
        const amount =  moneyFormatter.mapStringToPenniesNumber(
            formModel.eventType == INCOME_EVENT_TYPE ? formModel.amount : -formModel.amount
        );

        let body;
        if (cyclic) {
          body = new CreateCyclicEventRequest(
              formModel.title,
              amount,
              new Date(formModel.begins).getTime(),
              new Date(formModel.ends).getTime(),
              formModel.cycleBase,
              formModel.cycleLength
          );
        } else {
          body = new CreateOneTimeEventRequest(
              formModel.title,
              amount,
              new Date(formModel.date).getTime(),
          );
        }

        return httpService.post("/events", body)
            .then(res => {
              switch (res.status) {
                case HTTP_CREATED:
                  this._events.push(res.body);
                  this._events.sort(compare);
                  this._notifyChangeListeners();
                  break;
                default:
                  return httpService.onUnexpectedHttpStatus(res.status);
              }
              return res;
            });
    },

    delete(id) {
        return httpService.delete(`/events/${id}`)
            .then(async res => {
              switch (res.status) {
                case HTTP_NO_CONTENT:
                  this._events = this._events.filter(events => events.id !== id);
                  this._notifyChangeListeners();
                  break;
                case HTTP_NOT_FOUND:
                  await this.getList();
                  break;
                default:
                  return httpService.onUnexpectedHttpStatus(res.status);
              }
              return res;
            });
    },

    update(formModel) {
      const body = new UpdateEventRequest(
          formModel.title,
          moneyFormatter.mapStringToPenniesNumber(
              formModel.eventType == INCOME_EVENT_TYPE ? formModel.amount : -formModel.amount
          ),
          new Date(formModel.date).getTime()
      );
      return httpService.put(`/events/${formModel.id}`, body)
          .then(async res => {
            switch (res.status) {
              case HTTP_NO_CONTENT:
                this._replace_event(formModel.id, res.body);
                this._events.sort(compare);
                this._notifyChangeListeners();
                break;
              case HTTP_NOT_FOUND:
                await this.getList();
                break;
              default:
                return httpService.onUnexpectedHttpStatus(res.status);
            }
            return res;
          });
    },

    _replace_event(eventId, newEvent) {
      const eventIndex = this._events.findIndex(event => event.id === eventId);
      this._events[eventIndex] = newEvent;
    }
};
