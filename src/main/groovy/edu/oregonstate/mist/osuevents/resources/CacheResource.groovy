package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.osuevents.core.CacheObject
import edu.oregonstate.mist.osuevents.db.CacheDAO
import edu.oregonstate.mist.osuevents.db.EventsDAO
import io.dropwizard.auth.Auth
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/cache/update/')
@Produces(MediaType.APPLICATION_JSON)
class CacheResource extends Resource {
    private final CacheDAO cacheDAO
    private final EventsDAO eventsDAO

    CacheResource(CacheDAO cacheDAO, EventsDAO eventsDAO) {
        this.cacheDAO = cacheDAO
        this.eventsDAO = eventsDAO
    }

    @PUT
    @Path('customfields')
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomFields(@Auth AuthenticatedUser _) {
        def customFieldsDB = cacheObjectsToMap(eventsDAO.getCustomFields())
        def customFields = cacheDAO.getCustomFields()
        def changes = compareMapPair(customFieldsDB, customFields)

        changes.deleted.each {
            eventsDAO.deleteCustomField(it.key)
        }

        changes.new.each {
            eventsDAO.createCustomField(new CacheObject(
                    id: it.key,
                    name: it.value
            ))
        }

        changes.updated.each {
            eventsDAO.updateCustomField(new CacheObject(
                    id: it.key,
                    name: it.value
            ))

        }

        ok(changes).build()
    }

    @PUT
    @Path('departments')
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDepartments(@Auth AuthenticatedUser _) {
        def departmentsDB = cacheObjectsToMap(eventsDAO.getDepartments())
        def departments = cacheDAO.getDepartments()
        def changes = compareMapPair(departmentsDB, departments)

        changes.deleted.each {
            eventsDAO.deleteDepartment(it.key)
        }

        changes.new.each {
            eventsDAO.createDepartment(new CacheObject(
                    id: it.key,
                    name: it.value
            ))
        }

        changes.updated.each {
            eventsDAO.updateDepartment(new CacheObject(
                    id: it.key,
                    name: it.value
            ))

        }

        ok(changes).build()
    }

    @PUT
    @Path('filters')
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFilters(@Auth AuthenticatedUser _) {
        def filtersDB = cacheObjectsToMap(eventsDAO.getFilters())
        def filterChanges = compareMapPair(filtersDB, cacheDAO.getFilters())
        def allChanges = [:]

        filterChanges.deleted.each {
            eventsDAO.deleteFilter(it.key)
        }

        filterChanges.new.each {
            eventsDAO.createFilter(new CacheObject(
                    id: it.key,
                    name: it.value
            ))
        }

        filterChanges.updated.each {
            eventsDAO.updateFilter(new CacheObject(
                    id: it.key,
                    name: it.value
            ))

        }
        allChanges["filters"] = filterChanges

        def filterItemsAll = cacheDAO.getFilterItems()
        def allFilterItemChanges = [:]

        filterItemsAll.each { filterID, filterItemData ->
            def filterItemsDB = cacheObjectsToMap(eventsDAO.getFilterItems(filterID))

            def filterItems = [:]
            filterItemData.each {
                filterItems[new String ("${it.id}")] = new String ("${it.name}")
            }

            def filterItemChanges = compareMapPair(filterItemsDB, filterItems)

            filterItemChanges.deleted.each {
                eventsDAO.deleteFilterItem(it.key, filterID)
            }

            filterItemChanges.new.each {
                eventsDAO.createFilterItem(new CacheObject(
                        id: it.key,
                        name: it.value
                ), filterID)
            }

            filterItemChanges.updated.each {
                eventsDAO.updateFilterItem(new CacheObject(
                        id: it.key,
                        name: it.value
                ), filterID)

            }
            allFilterItemChanges["${filterID}"] = filterItemChanges
        }
        allChanges["filterItems"] = allFilterItemChanges

        ok(allChanges).build()
    }

    @PUT
    @Path('groups')
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGroups(@Auth AuthenticatedUser _) {
        def groupsDB = cacheObjectsToMap(eventsDAO.getGroups())
        def groups = cacheDAO.getGroups()
        def changes = compareMapPair(groupsDB, groups)

        changes.deleted.each {
            eventsDAO.deleteGroup(it.key)
        }

        changes.new.each {
            eventsDAO.createGroup(new CacheObject(
                    id: it.key,
                    name: it.value
            ))
        }

        changes.updated.each {
            eventsDAO.updateGroup(new CacheObject(
                    id: it.key,
                    name: it.value
            ))

        }

        ok(changes).build()
    }

    @PUT
    @Path('places')
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePlaces(@Auth AuthenticatedUser _) {
        def placesDB = cacheObjectsToMap(eventsDAO.getPlaces())
        def places = cacheDAO.getPlaces()
        def changes = compareMapPair(placesDB, places)

        changes.deleted.each {
            eventsDAO.deletePlace(it.key)
        }

        changes.new.each {
            eventsDAO.createPlace(new CacheObject(
                        id: it.key,
                        name: it.value
                ))
        }

        changes.updated.each {
            eventsDAO.updatePlace(new CacheObject(
                        id: it.key,
                        name: it.value
                ))

        }

        ok(changes).build()
    }

    private def cacheObjectsToMap(List<CacheObject> cacheObjects) {
        def cacheObjectMap = [:]

        cacheObjects.each {
            cacheObjectMap[new String("${it.id}")] = new String("${it.name}")
        }
        cacheObjectMap
    }

    private def compareMapPair(def oldMap, def newMap) {
        def newKeys = newMap*.key
        def oldKeys = oldMap*.key

        def removedKeys = oldKeys - newKeys
        def addedKeys = newKeys - oldKeys
        def commonKeys = newKeys - removedKeys - addedKeys
        def changedKeys = commonKeys.findAll { oldMap[it] != newMap[it] }
        def unchangedKeys = commonKeys - changedKeys

        [
                deleted: oldMap.findAll { it.key in removedKeys },
                new: newMap.findAll { it.key in addedKeys },
                updated: newMap.findAll { it.key in changedKeys },
                unchanged: newMap.findAll { it.key in unchangedKeys }
        ]
    }
}
