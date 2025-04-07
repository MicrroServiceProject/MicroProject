import { BlogComment } from "./comment.model";

export interface Post {
  id: string;
  title: string;
  content: string;
  imageUrl: string;
  authorUsername: string;
  authorId?: string; // Make it optional
  createdAt: string;
  category: string;
  likes: number;
  isLiked: boolean;
  isFavorite: boolean;
  comments: BlogComment[];
  views: number;
}