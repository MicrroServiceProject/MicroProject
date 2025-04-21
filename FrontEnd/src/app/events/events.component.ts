import { Component, OnInit } from "@angular/core"
import { Router, ActivatedRoute } from "@angular/router"
import { EventService } from "../services/event-services/event.service"
import { UserService } from "../services/user.service"
import { Event } from "../models/events.model"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { DatePipe } from "@angular/common"
import { Artiste } from "../models/artiste.model"
import { ArtisteService } from "../services/ArtisteService.service"

@Component({
  selector: "app-events",
  templateUrl: "./events.component.html",
  styleUrls: ["./events.component.scss"],
  providers: [DatePipe],
})
export class EventsComponent implements OnInit {
  showEventsList = false
  currentUserId: number | undefined
  eventForm: FormGroup
  isEditMode = false
  artistes: Artiste[] = []

  constructor(
    private eventService: EventService,
    private artisteService: ArtisteService,
    private router: Router,
    private route: ActivatedRoute,
    private userService: UserService,
    private fb: FormBuilder,
    private datePipe: DatePipe,
  ) {
    this.eventForm = this.createEventForm()
  }

  ngOnInit(): void {
    const currentUser = this.userService.getCurrentUser()
    this.currentUserId = currentUser?.userId

    // Load artists for the dropdown
    this.loadArtistes()

    this.route.queryParams.subscribe((params) => {
      if (params["id"]) {
        this.isEditMode = true

        // Convert the event data from query params to form values
        const eventDate = params["date"] ? new Date(params["date"]) : null

        this.eventForm.patchValue({
          id: params["id"],
          nom: params["nom"] || "",
          description: params["description"] || "",
          location: params["location"] || "",
          date: eventDate ? this.formatDateTimeForInput(eventDate) : "",
          capaciteMax: +params["capaciteMax"] || 0,
          artisteId: params["artisteId"] || "",
          tags: params["tags"] ? params["tags"].split(',') : []
        })
      }
    })
  }

  loadArtistes(): void {
    this.artisteService.getAllArtistes().subscribe({
      next: (artistes) => {
        this.artistes = artistes
      },
      error: (err) => {
        console.error('Failed to load artists', err)
      }
    })
  }

  createEventForm(): FormGroup {
    return this.fb.group({
      id: [null],
      nom: ["", Validators.required],
      description: ["", [Validators.maxLength(500)]],
      location: [""],
      date: ["", [Validators.required]],
      capaciteMax: [0, [Validators.required, Validators.min(1)]],
      artisteId: ["", Validators.required],
      tags: [[]]
    })
  }

  formatDateTimeForInput(date: Date): string {
    return this.datePipe.transform(date, "yyyy-MM-ddTHH:mm") || ""
  }

  onSubmit(): void {
    if (this.eventForm.invalid) {
      return
    }

    const tagsValue = this.eventForm.value.tags;
    const tagsArray = typeof tagsValue === 'string' ? 
                     tagsValue.split(',').map((tag: string) => tag.trim()) : 
                     tagsValue;

    const eventData: Event = {
      ...this.eventForm.value,
      date: new Date(this.eventForm.value.date),
      tags: tagsArray,
      placesReservees: 0
    }

    const operation = this.isEditMode
      ? this.eventService.updateEvent(eventData)
      : this.eventService.createEvent(eventData, eventData.artisteId!)

    operation.subscribe({
      next: (response) => {
        alert(`Event ${this.isEditMode ? "updated" : "created"} successfully!`)
        this.resetForm()
        this.router.navigate(["/events"])
      },
      error: (err) => {
        console.error("Error processing event", err)
        alert(`Failed to ${this.isEditMode ? "update" : "create"} event.`)
      },
    })
  }

  resetForm(): void {
    this.eventForm.reset({
      capaciteMax: 0,
      tags: []
    })
    this.isEditMode = false
  }

  toggleEventsList(): void {
    this.router.navigate(["/events"])
  }

  onJoinEvent(event: Event): void {
    if (event.id) {
      this.router.navigate(["/reservation", event.id])
    }
  }
}