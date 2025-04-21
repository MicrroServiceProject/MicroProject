import { User } from './user.model';
import { Artiste } from './artiste.model';

export interface Event {
  id?: string; // optionnel si tu utilises MongoDB ou UUID
  nom: string;
  description: string;
  location: string;
  date: string; // en string si tu reçois une ISO date depuis l'API
  endTime: string;
  capaciteMax: number;
  hostedBy: string;
  placesReservees?: number;
  placesDisponibles?: number;
  tags?: string[];
  artiste?: Artiste;
  artisteId?: string;
}
