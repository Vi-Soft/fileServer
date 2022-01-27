package com.visoft.file.service.dto;

public enum Type {

    CHECKLIST("Checklist"),
    APPROVAL_OF_SUBCONTRACTORS("Approval_Of_Sub-Contractor"),
    APPROVAL_OF_MATERIALS("Preliminary_Materials_Inspection"),
    APPROVAL_OF_SUPPLIERS("Approval_of_Supplier"),
    POC("POC"),
    NCR_IL("NCR"),
    RFI_IL("RFI"),
    SUPERVISION_REPORTS("Supervision_Reports"),
    AS_MADE("As_Made"),
    MEETING_SUMMARY("Meetings_Summary"),
    QC_AUDITS("QC_Audits"),
    MONTHLY_REPORTS("Monthly_Reports"),
    CONSTRUCTION_DOCUMENTS("Construction_Documents"),
    ADDITIONAL_DOCS("Additional_Documents"),
    DRAWINGS_IL("Drawings"),
    DEFAULT("Default"),
    LAB_ORDER("Lab_Order"),
    SURVEYING_ORDER("Surveying_Order"),
    CAR_PAR_OBS("CAR_PAR_OBS"),
    HWS_CERTIFICATE("HWS_CERTIFICATE"),
    CM_ATTACHMENT("CM_ATTACHMENT");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equalsName(String otherValue) {
        return value.equals(otherValue);
    }

    public String toString() {
        return this.value;
    }
}