import { httpService } from "./http-service"

export const eventService = {
    getEventsList() { 
        return httpService.post("/event/list", null)
        .then(res => this.setEvents(res))
    },
    setEvents(res){
        var eventData = res.body
        console.log(eventData)
        return eventData
    }
}

