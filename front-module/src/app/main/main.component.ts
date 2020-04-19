import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpEventType, HttpHeaders, HttpRequest } from '@angular/common/http';
import { AppService } from '../app.service';
import { Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../environments/environment';

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

  model32: ModelFromJar;
  model64: ModelFromJar;

  @ViewChild('textArea32') private textArea32: ElementRef;
  @ViewChild('textArea64') private textArea64: ElementRef;

  text: string;

  sizeError = false;
  fileFormatError: boolean;
  fileSize = 5000000;
  stage: 'drop' | 'progress' = 'drop';
  url: string;

  @ViewChild('imageFile') imageFile: ElementRef;

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

  monitor() {
    this.router.navigate(['/monitor']);
  }

  localFile(event) {
    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);

    reader.onload = (event: any) => {
      this.url = event.target.result;
    };
  }

  onFileChange(imageFile = this.imageFile) {
    this.sizeError = false;
    this.fileFormatError = false;
    if (imageFile.nativeElement.files.length) {
      const file: File = imageFile.nativeElement.files[0];

      if (file.size > this.fileSize) {
        this.sizeError = true;
        return;
      } else {
        this.treatmentFiles(file);
        this.stage = 'progress';
      }
    }
  }

  treatmentFiles(file: File) {
    const reader = new FileReader();
    reader.readAsArrayBuffer(file);
    reader.onload = () => {
      let url: string;
      url = environment.baseUrl + '/images';
      let request = this.loadPhotoWithPercentage(file, url);
      request.subscribe((event: any) => {
        if (event.type === HttpEventType.Response) {
          const image: any = event.body;
          image.name = file.name;
        }
      });
    };
  }

  loadPhotoWithPercentage(file: File, url?: string, order?: number) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('is_main', 'false');
    formData.append('is_public', 'true');
    order ? formData.append('order', order.toString()) : null;
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
    headers.append('Accept', 'application/json');

    const request = new HttpRequest('POST', url, formData, {reportProgress: true, headers});

    return this.http.request(request)
      .pipe(
        switchMap((event: any) => {
          return of(event);
        })
      );
  }
}
