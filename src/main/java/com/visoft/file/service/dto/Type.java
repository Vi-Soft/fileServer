package com.visoft.file.service.dto;

public enum Type {

    CHECKLIST("Checklist", "רשימת תיוג"),
    APPROVAL_OF_SUBCONTRACTORS("Approval_Of_Sub-Contractor", "אישור קבלני משנה"),
    APPROVAL_OF_MATERIALS("Preliminary_Materials_Inspection", "בקרה מקדימה לחומרים"),
    APPROVAL_OF_SUPPLIERS("Approval_of_Supplier", "אישור ספקים"),
    POC("POC", "דו_ח קטעי ניסוי"),
    NCR_IL("NCR", "טופס אי התאמה"),
    RFI_IL("RFI", ""),
    SUPERVISION_REPORTS("Supervision_Reports", "דוחות פיקוח עליון"),
    AS_MADE("As_Made", ""),
    MEETING_SUMMARY("Meetings_Summary", ""),
    QC_AUDITS("QC_Audits", "מבדקים פנימיים בקרת איכות"),
    MONTHLY_REPORTS("Monthly_Reports", ""),
    CONSTRUCTION_DOCUMENTS("Construction_Documents", ""),
    ADDITIONAL_DOCS("Additional_Documents", ""),
    DRAWINGS_IL("Drawings", ""),
    DEFAULT("Default", ""),
    LAB_ORDER("Lab_Order", ""),
    SURVEYING_ORDER("Surveying_Order", ""),
    CAR_PAR_OBS("CAR_PAR_OBS", ""),
    CM_TASK_EXECUTION_APPROVAL("CM_TASK_EXECUTION_APPROVAL", ""),
    CM_ATTACHMENT("CM_ATTACHMENT", ""),
    QUALITY_PLAN_PROCEDURE("Quality_Plan_Procedure", "תוכנית בקרת איכות ונהלים"),
    OTHER_DOCUMENTS("Other_Documents", "מסמכי תיעוד QC אחרים"),
    PRELIMINARY("Preliminary", "בקרה מקדימה"),
    SUMMARY("Summary", "דוחות"),
    LAYERED_SYSTEMS("Layered_Systems", "Layered_Systems");

    private final String value;

    private final String hebrewName;

    Type(String value, String hebrewName) {
        this.value = value;
        this.hebrewName = hebrewName;
    }

    public String getValue() {
        return this.value;
    }

    public String getHebrewName() {
        return this.hebrewName;
    }

    public boolean equalsName(String otherValue) {
        return value.equals(otherValue);
    }

    public String toString() {
        return this.value;
    }

    public static Type findByHebrewName(String hebrewName){
        for(Type type : values()){
            if( type.hebrewName.equals(hebrewName)){
                return type;
            }
        }
        return null;
    }
}
