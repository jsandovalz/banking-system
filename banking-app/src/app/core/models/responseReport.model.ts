import { ResumeReport } from "./resumeReport.model";

export interface ResponseReport {
    report: ResumeReport;
    pdfBase64: string;
}
