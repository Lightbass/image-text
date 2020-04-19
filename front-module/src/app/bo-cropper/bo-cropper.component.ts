import Cropper from 'cropperjs/dist/cropper.esm.js';

import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';

@Component({
  selector: 'gm-bo-cropper',
  templateUrl: './bo-cropper.component.html',
  styleUrls: ['./bo-cropper.component.scss']
})
export class BoCropperComponent implements OnInit, AfterViewInit, OnDestroy {

  cropper: Cropper;

  @ViewChild('cropImage') cropImage: ElementRef;
  @ViewChild('canvas') canvas: ElementRef;

  @Input() imageUrl: string;
  aspectRation = 1;

  @Output() croppedImageEvent = new EventEmitter<any>();
  @Output() closeModalEvent = new EventEmitter();
  @Output() imageWasLoaded: EventEmitter<void> = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.cropper = new Cropper(this.cropImage.nativeElement, {
      aspectRatio: this.aspectRation,
      viewMode: 1,
      rotatable: true,
      checkOrientation: true
    });
  }

  ngOnDestroy(): void {
    this.cropper.destroy();
  }

  getCroppedData() {
    const data = this.cropper.crop().cropBoxData;
    const canvasData = this.cropper.getCanvasData();

    const top = Math.ceil(data.top - data.minTop);
    const left = Math.ceil(data.left - data.minLeft);
    const maxWidth = canvasData.width;
    const maxHeight = canvasData.height;

    const naturalWidth = Math.ceil(canvasData.naturalWidth);
    const naturalHeight = Math.ceil(canvasData.naturalHeight);

    const kWidth = naturalWidth / maxWidth;
    const kHeight = naturalHeight / maxHeight;

    const crop: any = {
      width: Math.ceil(data.width * kWidth),
      height: Math.ceil(data.height * kHeight),
      quality: 100,
      x: Math.ceil(left * kWidth),
      y: Math.ceil(top * kHeight)
    };
    console.log(crop);


    // this.loader.loadCrop(crop, this.imageId)
    //   .subscribe((image: ImageModel) => {
    //     this.imageWasLoaded.emit();
    //     this.croppedImageEvent.emit(image);
    //   });
  }

  closeModal() {
    this.closeModalEvent.emit();
  }

  cropperTouchStart(event) {
    event.stopPropagation();
    event.preventDefault();
  }
}
