import { httpService } from "./http-service"
import { EventStorge } from "./events-storage";
import { HTTP_OK } from "../utils/http-status";

export const eventService = {
    getEventsList() { 
        return httpService.post("/event/list", null)
        .then(res => this.setEvents(res))
        .catch(error => console.log(error))
    },
    setEvents(res){
        if (res.status === HTTP_OK) {
            EventStorge.accessEvents = res.body.list;
        }
        return res;
    }
}

