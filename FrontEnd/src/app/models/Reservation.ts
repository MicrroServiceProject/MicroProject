export interface Reservation {
    id?: string;
    dateReservation?: Date;
    nbrPlace: number;
    statut?: StatutReservation;
    evenementId: string;
    clientId: string;
    evenement?: Evenement;
    client?: Client;
  }
  
  export enum StatutReservation {
    ACCEPTE = 'ACCEPTE',
    REFUSE = 'REFUSE',
    EN_COURS = 'EN_COURS',
    ANNULEE = 'ANNULEE'
  }
  
  export interface Evenement {
    id: string;
    nom: string;
    date: Date;
    description: string;
    capaciteMax: number;
    placesReservees: number;
    location: string;
    artisteId: string;
    tags: string[];
  }
  
  export interface Client {
    id: string;
    nom: string;
    prenom: string;
    email: string;
  }