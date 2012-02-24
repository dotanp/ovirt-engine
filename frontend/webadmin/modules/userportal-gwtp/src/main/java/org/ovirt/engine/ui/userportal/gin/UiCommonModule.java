package org.ovirt.engine.ui.userportal.gin;

import org.ovirt.engine.ui.common.gin.BaseUiCommonModule;
import org.ovirt.engine.ui.uicommonweb.models.userportal.UserPortalLoginModel;
import org.ovirt.engine.ui.userportal.uicommon.UserPortalConfigurator;
import org.ovirt.engine.ui.userportal.uicommon.model.UserPortalListProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.UserPortalModelResolver;
import org.ovirt.engine.ui.userportal.uicommon.model.basic.UserPortalBasicListProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateDiskListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateEventListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateGeneralModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateInterfaceListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplatePermissionListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.UserPortalTemplateListProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.vm.VmDiskListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.vm.VmGeneralModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.vm.VmInterfaceListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.vm.VmPermissionListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.vm.VmSnapshotListModelProvider;

import com.google.inject.Singleton;

/**
 * GIN module containing UserPortal UiCommon model and integration bindings.
 */
public class UiCommonModule extends BaseUiCommonModule {

    @Override
    protected void configure() {
        bindModels();
        bindIntegration();
    }

    void bindModels() {
        // Basic tab
        bind(UserPortalBasicListProvider.class).asEagerSingleton();

        // Extended tab: Virtual Machine
        bind(UserPortalListProvider.class).asEagerSingleton();
        bind(VmGeneralModelProvider.class).asEagerSingleton();
        bind(VmInterfaceListModelProvider.class).asEagerSingleton();
        bind(VmDiskListModelProvider.class).asEagerSingleton();
        bind(VmSnapshotListModelProvider.class).asEagerSingleton();
        bind(VmPermissionListModelProvider.class).asEagerSingleton();

        // Extended tab: Template
        bind(UserPortalTemplateListProvider.class).asEagerSingleton();
        bind(TemplateGeneralModelProvider.class).asEagerSingleton();
        bind(TemplateInterfaceListModelProvider.class).asEagerSingleton();
        bind(TemplateDiskListModelProvider.class).asEagerSingleton();
        bind(TemplateEventListModelProvider.class).asEagerSingleton();
        bind(TemplatePermissionListModelProvider.class).asEagerSingleton();
    }

    void bindIntegration() {
        bindCommonIntegration();
        bindConfiguratorIntegration(UserPortalConfigurator.class);
        bind(UserPortalModelResolver.class).in(Singleton.class);
        bind(UserPortalLoginModel.class).in(Singleton.class);
    }

}
