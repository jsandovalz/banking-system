import { AccountType } from "../types/account-type";

export interface Account {
    id?:number;
    accountNumber: string;
    accountType: AccountType;
    initialBalance: number;
    availableBalance?: number;
    status: boolean;
    clientId: string;
    nameClient?:string;
}