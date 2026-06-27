import { AccountType } from "../types/account-type";
import { TransactionType } from "../types/transaction-type";

export interface ResumeReport {
    client: string;
    from: string;
    to: string;
    totalCredits: number;
    totalDebits: number;
    transactions: Array<{
        date: string;
        client: string;
        accountNumber: string;
        accountType: AccountType;
        initialBalance: number;
        status: boolean;
        transactionType: TransactionType;
        amount: number;
        balance: number;
    }>;
}