package edu.oregonstate.mist.osuevents.health

import com.codahale.metrics.health.HealthCheck
import edu.oregonstate.mist.osuevents.db.EventsDAO

class EventsHealthCheck extends HealthCheck {
    private final EventsDAO eventsDAO

    public EventsHealthCheck(EventsDAO eventsDAO) {
        this.eventsDAO = eventsDAO
    }

    @Override
    protected Result check() throws Exception {
        try {
            String status = eventsDAO.checkHealth()

            if (status != null) {
                return Result.healthy()
            }
            Result.unhealthy("status: ${status}")
        } catch(Exception e) {
            Result.unhealthy(e.message)
        }
    }
}