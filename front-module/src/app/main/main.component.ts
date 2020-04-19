import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpEventType, HttpHeaders, HttpRequest } from '@angular/common/http';
import { AppService } from '../app.service';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import * as FileSaver from 'file-saver';

interface ModelFromJar {
  timestamp: number;
  status: string;
  output: string;
}

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  text: string;
  mode: 'text' | 'crop' = 'text';

  sizeError = false;
  fileFormatError: boolean;
  fileSize = 5000000;
  stage: 'drop' | 'progress' = 'drop';
  url: string;

  @ViewChild('imageFile') imageFile: ElementRef;

  file: File;

  constructor(protected http: HttpClient,
              private appService: AppService,
              private router: Router) { }

  ngOnInit(): void {
    this.getStatus();
    setInterval(() => this.getStatus(), 1000);
  }

  getStatus() {
    const url = `${this.appService.baseUrl}/api/front`;
    // this.http
    //   .get(url)
    //   .toPromise()
    //   .then((res: any) => {
    //     this.model32 = res.model32;
    //     this.model64 = res.model64;
    //     try {
    //       this.textArea32.nativeElement.scrollTop = this.textArea32.nativeElement.scrollHeight;
    //       this.textArea64.nativeElement.scrollTop = this.textArea64.nativeElement.scrollHeight;
    //     } catch (err) {}
    //     return res;
    //   })
    //   .catch((res) => {
    //     return Promise.reject(res);
    //   } );
  }

  executeButtonClick() {
    const url = `${this.appService.baseUrl}/allo`;
    this.http.get(url).toPromise().then((res: any) => {
      this.text = res.appName;
      return res;
    }).catch((res) => {
      return Promise.reject(res);
    });
  }

  cropMode() {
    this.mode = 'crop';
  }

  localFile(event) {
    const reader = new FileReader();
    console.log(event);
    this.file = event.target.files[0];
    reader.readAsDataURL(event.target.files[0]);

    reader.onload = (ev: any) => {
      this.url = ev.target.result;
    };
  }

  onFileChange(crop: any) {
    this.sizeError = false;
    this.fileFormatError = false;
    const file: File = this.file;

    if (file.size > this.fileSize) {
      this.sizeError = true;
      return;
    } else {
      this.treatmentFiles(file, crop);
      this.stage = 'progress';
    }
  }

  treatmentFiles(file: File, crop: any) {
    this.url = null;
    const reader = new FileReader();
    reader.readAsArrayBuffer(file);
    reader.onload = () => {
      let url: string;
      url = environment.baseUrl + '/images';
      const request = this.loadPhotoWithPercentage(file, url, crop);
      request.subscribe((event: any) => {
        if (event.type === HttpEventType.Response) {
          FileSaver.saveAs(event.body, 'content.jpg');
          this.mode = 'text';
        }
      });
    };
  }

  loadPhotoWithPercentage(file: File, url?: string, crop?: any) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('size', crop.width);
    formData.append('x', crop.x);
    formData.append('y', crop.y);
    formData.append('text', this.text);
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
    headers.append('Accept', 'image/jpeg');

    const request = new HttpRequest('POST', url, formData, {headers, responseType: 'blob'});

    return this.http.request(request);
  }
}
