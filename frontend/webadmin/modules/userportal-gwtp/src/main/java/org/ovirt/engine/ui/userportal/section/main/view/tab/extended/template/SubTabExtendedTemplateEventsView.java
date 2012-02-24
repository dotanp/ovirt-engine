package org.ovirt.engine.ui.userportal.section.main.view.tab.extended.template;

import org.ovirt.engine.core.common.businessentities.AuditLog;
import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.system.ClientStorage;
import org.ovirt.engine.ui.common.view.AbstractSubTabTableWidgetView;
import org.ovirt.engine.ui.common.widget.uicommon.events.EventListModelTable;
import org.ovirt.engine.ui.uicommonweb.models.templates.TemplateEventListModel;
import org.ovirt.engine.ui.uicommonweb.models.userportal.UserPortalTemplateListModel;
import org.ovirt.engine.ui.userportal.section.main.presenter.tab.extended.template.SubTabExtendedTemplateEventsPresenter;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateEventListModelProvider;
import org.ovirt.engine.ui.userportal.widget.table.column.UserPortalAuditLogSeverityColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class SubTabExtendedTemplateEventsView extends AbstractSubTabTableWidgetView<VmTemplate, AuditLog, UserPortalTemplateListModel, TemplateEventListModel>
        implements SubTabExtendedTemplateEventsPresenter.ViewDef {

    interface ViewIdHandler extends ElementIdHandler<SubTabExtendedTemplateEventsView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @Inject
    public SubTabExtendedTemplateEventsView(TemplateEventListModelProvider modelProvider,
            EventBus eventBus, ClientStorage clientStorage) {
        super(new EventListModelTable<TemplateEventListModel>(modelProvider, eventBus,
                clientStorage, new UserPortalAuditLogSeverityColumn()));
        ViewIdHandler.idHandler.generateAndSetIds(this);
        initTable();
        initWidget(getModelBoundTableWidget());
    }

}
