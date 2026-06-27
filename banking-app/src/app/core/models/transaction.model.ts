import { TransactionType } from "../types/transaction-type";

export interface Transaction {
    id?: number;
    date?: string;
    transactionType: TransactionType;
    amount: number;
    balance?: number;
    accountNumber: string;
}