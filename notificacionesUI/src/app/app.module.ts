import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http'
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import { ToastModule } from 'primeng/toast';
import { HomeComponent } from './home/home.component';
import {ButtonModule} from 'primeng/button';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {DialogModule} from 'primeng/dialog';
import {DividerModule} from 'primeng/divider';
import {InputTextModule} from 'primeng/inputtext';
import { MessageService } from 'primeng/api';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule, // Para hacer peticiones a backend
    FormsModule,
    MessageModule,
    MessagesModule,
    ToastModule,
    ButtonModule,
    BrowserAnimationsModule, //Para usar modales etc...
    DialogModule,
    InputTextModule,
    DividerModule,
  ],
  providers: [MessageService],
  bootstrap: [AppComponent]
})
export class AppModule { }
