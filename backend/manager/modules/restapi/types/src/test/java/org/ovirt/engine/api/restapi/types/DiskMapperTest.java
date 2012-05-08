package org.ovirt.engine.api.restapi.types;

import static org.ovirt.engine.api.restapi.types.MappingTestHelper.populate;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.ovirt.engine.api.common.util.StatusUtils;
import org.ovirt.engine.api.model.Disk;
import org.ovirt.engine.api.model.DiskFormat;
import org.ovirt.engine.api.model.DiskInterface;
import org.ovirt.engine.api.model.DiskStatus;
import org.ovirt.engine.core.common.businessentities.DiskImage;
import org.ovirt.engine.core.common.config.Config;

public class DiskMapperTest extends AbstractInvertibleMappingTest<Disk, DiskImage, DiskImage> {

    protected DiskMapperTest() {
        super(Disk.class, DiskImage.class, DiskImage.class);
    }

    @Override
    protected Disk postPopulate(Disk model) {
        model.setFormat(MappingTestHelper.shuffle(DiskFormat.class).value());
        model.setInterface(MappingTestHelper.shuffle(DiskInterface.class).value());
        model.setStatus(StatusUtils.create(MappingTestHelper.shuffle(DiskStatus.class)));
        model.setLunStorage(null);
        return model;
    }

    @Override
    protected void verify(Disk model, Disk transform) {
        assertNotNull(transform);
        assertEquals(model.getId(), transform.getId());
        assertEquals(model.getImageId(), transform.getImageId());
        assertEquals(model.getSize(), transform.getSize());
        assertEquals(model.getFormat(), transform.getFormat());
        assertEquals(model.getInterface(), transform.getInterface());
        assertEquals(model.isActive(), transform.isActive());
        assertEquals("unexpected status", model.getStatus().getState(), transform.getStatus().getState());
        assertEquals("unexpected sparse", model.isSparse(), transform.isSparse());
        assertEquals("unexpected bootable", model.isBootable(), transform.isBootable());
        assertEquals("unexpected propagate errors", model.isPropagateErrors(), transform.isPropagateErrors());
        assertEquals("unexpected wipe after delete", model.isWipeAfterDelete(), transform.isWipeAfterDelete());
        assertEquals("unexpected shareable", model.isShareable(), transform.isShareable());
        assertEquals("unexpected allow snapshot", model.isAllowSnapshot(), transform.isAllowSnapshot());
    }

    @Test
    @Override
    public void testRoundtrip() throws Exception {
        mockStatic(Config.class);
        setUpConfigExpectations();
        replayAll();

        Disk model = Disk.class.cast(populate(Disk.class));
        model = postPopulate(model);
        Mapper<Disk, org.ovirt.engine.core.common.businessentities.Disk> out = getMappingLocator().getMapper(Disk.class, org.ovirt.engine.core.common.businessentities.Disk.class);
        Mapper<org.ovirt.engine.core.common.businessentities.Disk, Disk> back = getMappingLocator().getMapper(org.ovirt.engine.core.common.businessentities.Disk.class, Disk.class);
        DiskImage to = (DiskImage)out.map(model, null);
        DiskImage inverse = getInverse(to);
        Disk transform = back.map(inverse, null);
        verify(model, transform);
        verifyAll();
    }
}
