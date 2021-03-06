package org.ovirt.engine.api.restapi.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ovirt.engine.api.restapi.resource.BackendStorageDomainsResourceTest.getModel;
import static org.ovirt.engine.api.restapi.resource.BackendStorageDomainsResourceTest.setUpEntityExpectations;
import static org.ovirt.engine.api.restapi.resource.BackendStorageDomainsResourceTest.setUpStorageServerConnection;
import static org.ovirt.engine.api.restapi.resource.BackendStorageDomainsResourceTest.verifyModelSpecific;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.ovirt.engine.api.model.Action;
import org.ovirt.engine.api.model.LogicalUnit;
import org.ovirt.engine.api.model.LogicalUnits;
import org.ovirt.engine.api.model.StorageDomain;
import org.ovirt.engine.api.model.StorageDomainType;
import org.ovirt.engine.api.model.StorageType;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.ExtendSANStorageDomainParameters;
import org.ovirt.engine.core.common.action.ReduceSANStorageDomainDevicesCommandParameters;
import org.ovirt.engine.core.common.action.RemoveStorageDomainParameters;
import org.ovirt.engine.core.common.action.StorageDomainManagementParameter;
import org.ovirt.engine.core.common.action.StorageDomainParametersBase;
import org.ovirt.engine.core.common.businessentities.VdsStatic;
import org.ovirt.engine.core.common.businessentities.storage.LUNs;
import org.ovirt.engine.core.common.queries.GetLunsByVgIdParameters;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.NameQueryParameters;
import org.ovirt.engine.core.common.queries.QueryType;
import org.ovirt.engine.core.common.queries.StorageServerConnectionQueryParametersBase;

public class BackendStorageDomainResourceTest
        extends AbstractBackendSubResourceTest<StorageDomain, org.ovirt.engine.core.common.businessentities.StorageDomain, BackendStorageDomainResource> {

    public BackendStorageDomainResourceTest() {
        super(new BackendStorageDomainResource(GUIDS[0].toString(), new BackendStorageDomainsResource()));
    }

    @Override
    protected void init() {
        super.init();
        initResource(resource.getParent());
    }

    @Test
    public void testBadGuid() throws Exception {
        try {
            new BackendStorageDomainResource("foo", null);
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyNotFoundException(wae);
        }
    }

    @Test
    public void testGetNotFound() throws Exception {
        setUriInfo(setUpBasicUriExpectations());
        setUpGetEntityExpectations(1, true, getEntity(0));
        try {
            resource.get();
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyNotFoundException(wae);
        }
    }

    @Test
    public void testGet() throws Exception {
        setUpGetEntityExpectations(1, getEntity(0));
        setUpGetStorageServerConnectionExpectations(1);
        setUriInfo(setUpBasicUriExpectations());

        verifyModel(resource.get(), 0);
    }

    @Test
    public void testGetFcp() throws Exception {
        setUpGetEntityExpectations(1, getFcpEntity());
        setUpGetEntityExpectations(QueryType.GetLunsByVgId,
                GetLunsByVgIdParameters.class,
                new String[] { "VgId" },
                new Object[] { GUIDS[0].toString() },
                setUpLuns());
        setUriInfo(setUpBasicUriExpectations());
        verifyGetFcp(resource.get());
    }

    private void verifyGetFcp(StorageDomain model) {
        assertEquals(GUIDS[0].toString(), model.getId());
        assertEquals(NAMES[0], model.getName());
        assertEquals(StorageDomainType.DATA, model.getType());
        assertNotNull(model.getStorage());
        assertEquals(StorageType.FCP, model.getStorage().getType());
        assertNotNull(model.getLinks().get(0).getHref());
    }

    protected List<LUNs> setUpLuns() {
        LUNs lun = new LUNs();
        lun.setLUNId(GUIDS[2].toString());
        List<LUNs> luns = new ArrayList<>();
        luns.add(lun);
        return luns;
    }

    private org.ovirt.engine.core.common.businessentities.StorageDomain getFcpEntity() {
        org.ovirt.engine.core.common.businessentities.StorageDomain entity = mock(org.ovirt.engine.core.common.businessentities.StorageDomain.class);
        when(entity.getId()).thenReturn(GUIDS[0]);
        when(entity.getStorageName()).thenReturn(NAMES[0]);
        when(entity.getStorageDomainType()).thenReturn(org.ovirt.engine.core.common.businessentities.StorageDomainType.Data);
        when(entity.getStorageType()).thenReturn(org.ovirt.engine.core.common.businessentities.storage.StorageType.FCP);
        when(entity.getStorage()).thenReturn(GUIDS[0].toString());
        return entity;
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        setUriInfo(setUpBasicUriExpectations());
        setUpGetEntityExpectations(1, true, getEntity(0));
        try {
            resource.update(getModel(0));
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyNotFoundException(wae);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        setUpGetEntityExpectations(2, getEntity(0));
        setUpGetStorageServerConnectionExpectations(2);

        setUriInfo(setUpActionExpectations(ActionType.UpdateStorageDomain,
                                           StorageDomainManagementParameter.class,
                                           new String[] {},
                                           new Object[] {},
                                           true,
                                           true));

        verifyModel(resource.update(getModel(0)), 0);
    }

    @Test
    public void testUpdateCantDo() throws Exception {
        doTestBadUpdate(false, true, CANT_DO);
    }

    @Test
    public void testUpdateFailed() throws Exception {
        doTestBadUpdate(true, false, FAILURE);
    }

    private void doTestBadUpdate(boolean valid, boolean success, String detail) throws Exception {
        setUpGetEntityExpectations(1, getEntity(0));
        setUpGetStorageServerConnectionExpectations(1);

        setUriInfo(setUpActionExpectations(ActionType.UpdateStorageDomain,
                                           StorageDomainManagementParameter.class,
                                           new String[] {},
                                           new Object[] {},
                                           valid,
                                           success));

        try {
            resource.update(getModel(0));
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyFault(wae, detail);
        }
    }

    @Test
    public void testConflictedUpdate() throws Exception {
        setUpGetEntityExpectations(1, getEntity(0));
        setUpGetStorageServerConnectionExpectations(1);
        setUriInfo(setUpBasicUriExpectations());

        StorageDomain model = getModel(1);
        model.setId(GUIDS[1].toString());
        try {
            resource.update(model);
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            verifyImmutabilityConstraint(wae);
        }
    }

    @Test
    public void testRemoveStorageDomainNull() throws Exception {
        UriInfo uriInfo = setUpBasicUriExpectations();
        setUriInfo(uriInfo);
        try {
            resource.remove();
            fail("expected WebApplicationException");
        } catch (WebApplicationException wae) {
            assertEquals(400, wae.getResponse().getStatus());
        }
    }

    @Test
    public void testRemoveWithHostId() throws Exception {
        setUpGetEntityExpectations();
        UriInfo uriInfo = setUpActionExpectations(
            ActionType.RemoveStorageDomain,
            RemoveStorageDomainParameters.class,
            new String[] { "StorageDomainId", "VdsId", "DoFormat" },
            new Object[] { GUIDS[0], GUIDS[1], Boolean.FALSE },
            true,
            true,
            false
        );
        uriInfo = addMatrixParameterExpectations(uriInfo, BackendStorageDomainResource.HOST, GUIDS[1].toString());
        setUriInfo(uriInfo);
        verifyRemove(resource.remove());
    }

    @Test
    public void testRemoveWithFormat() throws Exception {
        setUpGetEntityExpectations();
        UriInfo uriInfo = setUpActionExpectations(
            ActionType.RemoveStorageDomain,
            RemoveStorageDomainParameters.class,
            new String[] { "StorageDomainId", "VdsId", "DoFormat" },
            new Object[] { GUIDS[0], GUIDS[1], Boolean.TRUE },
            true,
            true,
            false
        );
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendStorageDomainResource.HOST, GUIDS[1].toString());
        parameters.put(BackendStorageDomainResource.FORMAT, Boolean.TRUE.toString());
        uriInfo = addMatrixParameterExpectations(uriInfo, parameters);
        setUriInfo(uriInfo);
        verifyRemove(resource.remove());
    }

    @Test
    public void testRemoveWithDestroy() throws Exception {
        setUpGetEntityExpectations();
        UriInfo uriInfo = setUpActionExpectations(
            ActionType.ForceRemoveStorageDomain,
            StorageDomainParametersBase.class,
            new String[] { "StorageDomainId", "VdsId" },
            new Object[] { GUIDS[0], GUIDS[1] },
            true,
            true,
            false
        );
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendStorageDomainResource.HOST, GUIDS[1].toString());
        parameters.put(BackendStorageDomainResource.DESTROY, Boolean.TRUE.toString());
        uriInfo = addMatrixParameterExpectations(uriInfo, parameters);
        setUriInfo(uriInfo);
        verifyRemove(resource.remove());
    }

    @Test
    public void testRemoveWithHostName() throws Exception {
        setUpGetEntityExpectations();
        setUpGetEntityExpectations(
            QueryType.GetVdsStaticByName,
            NameQueryParameters.class,
            new String[] { "Name" },
            new Object[] { NAMES[1] },
            setUpVDStatic(1)
        );
        UriInfo uriInfo = setUpActionExpectations(
            ActionType.RemoveStorageDomain,
            RemoveStorageDomainParameters.class,
            new String[] { "StorageDomainId", "VdsId", "DoFormat" },
            new Object[] { GUIDS[0], GUIDS[1], Boolean.FALSE },
            true,
            true,
            false
        );
        uriInfo = addMatrixParameterExpectations(uriInfo, BackendStorageDomainResource.HOST, NAMES[1]);
        setUriInfo(uriInfo);
        verifyRemove(resource.remove());
    }

    @Test
    public void testRemoveCantDo() throws Exception {
        doTestBadRemove(false, true, CANT_DO);
    }

    @Test
    public void testRemoveFailed() throws Exception {
        doTestBadRemove(true, false, FAILURE);
    }

    protected void doTestBadRemove(boolean valid, boolean success, String detail) throws Exception {
        setUpGetEntityExpectations();
        UriInfo uriInfo = setUpActionExpectations(
            ActionType.RemoveStorageDomain,
            RemoveStorageDomainParameters.class,
            new String[] { "StorageDomainId", "VdsId", "DoFormat" },
            new Object[] { GUIDS[0], GUIDS[1], Boolean.FALSE },
            valid,
            success,
            false
        );
        uriInfo = addMatrixParameterExpectations(uriInfo, BackendStorageDomainResource.HOST, GUIDS[1].toString());
        setUriInfo(uriInfo);
        try {
            resource.remove();
            fail("expected WebApplicationException");
        }
        catch (WebApplicationException wae) {
            verifyFault(wae, detail);
        }
    }
    protected void setUpGetEntityExpectations(int times, org.ovirt.engine.core.common.businessentities.StorageDomain entity) throws Exception {
        setUpGetEntityExpectations(times, false, entity);
    }

    protected void setUpGetEntityExpectations(int times, boolean notFound, org.ovirt.engine.core.common.businessentities.StorageDomain entity) throws Exception {
        while (times-- > 0) {
            setUpGetEntityExpectations(QueryType.GetStorageDomainById,
                                       IdQueryParameters.class,
                                       new String[] { "Id" },
                                       new Object[] { GUIDS[0] },
                                       notFound ? null : entity);
        }
    }

    protected void setUpGetStorageServerConnectionExpectations(int times) throws Exception {
        while (times-- > 0) {
            setUpGetEntityExpectations(QueryType.GetStorageServerConnectionById,
                                       StorageServerConnectionQueryParametersBase.class,
                                       new String[] { "ServerConnectionId" },
                                       new Object[] { GUIDS[0].toString() },
                                       setUpStorageServerConnection(0));
        }
    }

    private void setUpGetEntityExpectations() throws Exception {
        setUpGetEntityExpectations(QueryType.GetStorageDomainById,
                IdQueryParameters.class,
                new String[] { "Id" },
                new Object[] { GUIDS[0] },
                new org.ovirt.engine.core.common.businessentities.StorageDomain());
    }

    @Override
    protected org.ovirt.engine.core.common.businessentities.StorageDomain getEntity(int index) {
        return setUpEntityExpectations(mock(org.ovirt.engine.core.common.businessentities.StorageDomain.class), index);
    }

    @Override
    protected void verifyModel(StorageDomain model, int index) {
        verifyModelSpecific(model, index);
        verifyLinks(model);
    }

    protected VdsStatic setUpVDStatic(int index) {
        VdsStatic vds = new VdsStatic();
        vds.setId(GUIDS[index]);
        vds.setName(NAMES[index]);
        return vds;
    }

    @Test
    public void testRefreshLunsSize() throws Exception {
        List<String> lunsArray = new ArrayList();
        lunsArray.add(GUIDS[2].toString());
        setUriInfo(setUpActionExpectations(ActionType.RefreshLunsSize,
                ExtendSANStorageDomainParameters.class,
                new String[]{"LunIds"},
                new Object[]{lunsArray},
                true,
                true));
        Action action = new Action();
        LogicalUnits luns= new LogicalUnits();
        LogicalUnit lun = new LogicalUnit();
        lun.setId(GUIDS[2].toString());
        luns.getLogicalUnits().add(lun);
        action.setLogicalUnits(luns);
        verifyActionResponse(resource.refreshLuns(action));
    }

    @Test
    public void reduceLuns() throws Exception {
        List<String> paramsLuns = new LinkedList<>();
        paramsLuns.add(GUIDS[2].toString());
        paramsLuns.add(GUIDS[3].toString());
        setUriInfo(setUpActionExpectations(ActionType.ReduceSANStorageDomainDevices,
                ReduceSANStorageDomainDevicesCommandParameters.class,
                new String[]{"DevicesToReduce", "StorageDomainId"},
                new Object[]{paramsLuns, GUIDS[0]},
                true,
                true));
        Action action = new Action();
        LogicalUnits luns= new LogicalUnits();

        paramsLuns.forEach(s -> {
            LogicalUnit lun = new LogicalUnit();
            lun.setId(s);
            luns.getLogicalUnits().add(lun);
        });

        action.setLogicalUnits(luns);
        verifyActionResponse(resource.reduceLuns(action));
    }

    private void verifyActionResponse(Response response) throws Exception {
        verifyActionResponse(response, "storagedomains/" + GUIDS[0], false);
    }
}
