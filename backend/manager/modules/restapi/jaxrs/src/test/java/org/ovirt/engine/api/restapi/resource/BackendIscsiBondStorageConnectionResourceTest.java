package org.ovirt.engine.api.restapi.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.ovirt.engine.api.model.StorageConnection;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.EditIscsiBondParameters;
import org.ovirt.engine.core.common.businessentities.IscsiBond;
import org.ovirt.engine.core.common.businessentities.StorageServerConnections;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.QueryType;
import org.ovirt.engine.core.common.queries.StorageServerConnectionQueryParametersBase;
import org.ovirt.engine.core.compat.Guid;

public class BackendIscsiBondStorageConnectionResourceTest extends AbstractBackendSubResourceTest<StorageConnection, StorageServerConnections, BackendIscsiBondStorageConnectionResource> {

    protected static final Guid ISCSI_BOND_ID = GUIDS[1];
    protected static final Guid STORAGE_CONNECTION_ID = GUIDS[2];

    @Override
    protected void init() {
        super.init();
        initResource(resource.getParent());
    }

    public BackendIscsiBondStorageConnectionResourceTest() {
        super(new BackendIscsiBondStorageConnectionResource(STORAGE_CONNECTION_ID.toString(),
                new BackendIscsiBondStorageConnectionsResource(ISCSI_BOND_ID.toString())));
    }

    @Test
    public void testGet() throws Exception {
        setUriInfo(setUpBasicUriExpectations());
        setUpEntityQueryExpectations(1, getIscsiBondContainingStorageConnection());

        setUpEntityQueryExpectations(QueryType.GetStorageServerConnectionById,
                StorageServerConnectionQueryParametersBase.class,
                new String[] { "ServerConnectionId" },
                new Object[] { STORAGE_CONNECTION_ID.toString() },
                getEntity(0));


        StorageConnection model = resource.get();
        assertEquals(GUIDS[0].toString(), model.getId());
        verifyLinks(model);
    }

    @Test
    public void testGetWithInvalidStorageId() throws Exception {
        setUriInfo(setUpBasicUriExpectations());
        setUpEntityQueryExpectations(1, getIscsiBondWithNoMatchingStorages());

        try {
            resource.get();
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyNotFoundException(wae);
        }
    }

    @Test
    public void testGetStorageConnectionNotFound() throws Exception {
        setUriInfo(setUpBasicUriExpectations());
        setUpEntityQueryExpectations(1, getIscsiBondContainingStorageConnection());

        setUpEntityQueryExpectations(QueryType.GetStorageServerConnectionById,
                StorageServerConnectionQueryParametersBase.class,
                new String[] { "ServerConnectionId" },
                new Object[] { STORAGE_CONNECTION_ID.toString() },
                null);

        try {
            resource.get();
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyNotFoundException(wae);
        }
    }

    @Test
    public void testRemove() throws Exception {
        setUriInfo(setUpBasicUriExpectations());
        setUpEntityQueryExpectations(2, getIscsiBondContainingStorageConnection());
        setUpEntityQueryExpectations(
            QueryType.GetStorageServerConnectionById,
            StorageServerConnectionQueryParametersBase.class,
            new String[] { "ServerConnectionId" },
            new Object[] { STORAGE_CONNECTION_ID.toString() },
            getEntity(0)
        );
        setUpActionExpectations(
            ActionType.EditIscsiBond,
            EditIscsiBondParameters.class,
            new String[] { "IscsiBond" },
            new Object[] { getIscsiBond() },
            true,
            true,
            null
        );
        Response response = resource.remove();
        assertEquals(200, response.getStatus());
    }

    protected void setUpEntityQueryExpectations(int times, IscsiBond iscsiBond) throws Exception {
        while (times-- > 0) {
            setUpEntityQueryExpectations(QueryType.GetIscsiBondById,
                    IdQueryParameters.class,
                    new String[] { "Id" },
                    new Object[] { ISCSI_BOND_ID },
                    iscsiBond);
        }
    }

    private org.ovirt.engine.core.common.businessentities.IscsiBond getIscsiBondContainingStorageConnection() {
        org.ovirt.engine.core.common.businessentities.IscsiBond iscsiBond =
                new org.ovirt.engine.core.common.businessentities.IscsiBond();
        iscsiBond.setId(ISCSI_BOND_ID);
        iscsiBond.getStorageConnectionIds().add(STORAGE_CONNECTION_ID.toString());
        return iscsiBond;
    }

    private org.ovirt.engine.core.common.businessentities.IscsiBond getIscsiBondWithNoMatchingStorages() {
        org.ovirt.engine.core.common.businessentities.IscsiBond iscsiBond =
                new org.ovirt.engine.core.common.businessentities.IscsiBond();
        iscsiBond.setId(ISCSI_BOND_ID);
        iscsiBond.getStorageConnectionIds().add(GUIDS[0].toString());
        return iscsiBond;
    }

    @Override
    protected StorageServerConnections getEntity(int index) {
        StorageServerConnections cnx = new StorageServerConnections();
        cnx.setId(GUIDS[index].toString());
        cnx.setConnection("10.11.12.13" + ":" + "/1");
        return cnx;
    }

    private org.ovirt.engine.core.common.businessentities.IscsiBond getIscsiBond() {
        org.ovirt.engine.core.common.businessentities.IscsiBond iscsiBond =
            new org.ovirt.engine.core.common.businessentities.IscsiBond();
        iscsiBond.setId(ISCSI_BOND_ID);
        return iscsiBond;
    }
}
