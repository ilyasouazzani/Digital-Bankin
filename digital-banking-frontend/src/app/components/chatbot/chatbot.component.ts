import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService } from '../../services/chatbot.service';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.component.html'
})
export class ChatbotComponent implements AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  messages: Message[] = [{
    role: 'assistant',
    content: 'Bonjour ! Je suis votre assistant bancaire IA. Je peux vous aider à consulter des informations sur vos comptes, comprendre les types de produits disponibles, ou répondre à vos questions bancaires. Comment puis-je vous aider ?',
    timestamp: new Date()
  }];

  userInput = '';
  loading   = false;

  constructor(private chatbotService: ChatbotService) {}

  ngAfterViewChecked(): void { this.scrollToBottom(); }

  sendMessage(): void {
    const content = this.userInput.trim();
    if (!content || this.loading) return;

    this.messages.push({ role: 'user', content, timestamp: new Date() });
    this.userInput = '';
    this.loading   = true;

    this.chatbotService.sendMessage(content).subscribe({
      next: res => {
        this.messages.push({ role: 'assistant', content: res.response, timestamp: new Date() });
        this.loading = false;
      },
      error: () => {
        this.messages.push({
          role: 'assistant',
          content: 'Je suis temporairement indisponible. Veuillez vérifier que le backend est démarré et que la clé OPENAI_API_KEY est configurée.',
          timestamp: new Date()
        });
        this.loading = false;
      }
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  clearChat(): void {
    this.chatbotService.clearHistory().subscribe();
    this.messages = [this.messages[0]];
  }

  private scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    } catch {}
  }
}
