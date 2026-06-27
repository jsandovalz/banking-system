export interface Client {
    id?: number;
    name:string;
    gender:'M' | 'F' | 'O';
    age: number;
    identification: string;
    address: string;
    phone: string;
    clientId:string;
    password:string;
    status: boolean;
}