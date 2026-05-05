import { useState, useRef, useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { aiApi } from '../services/api'
import { Send, Bot, User, Sparkles, Cpu, TrendingDown, Shield, AlertTriangle } from 'lucide-react'

interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  recommendation?: string
  sources?: Array<{ source: string; relevance: number }>
  timestamp: Date
}

export default function AIChat() {
  const [input, setInput] = useState('')
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'welcome',
      role: 'assistant',
      content: 'Hello! I am InfraRecord AI. I can help you analyze infrastructure health, optimize costs, review compliance, and assess security posture. What would you like to know?',
      timestamp: new Date(),
    }
  ])
  const [isLoading, setIsLoading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const { data: aiStatus } = useQuery({
    queryKey: ['ai-status'],
    queryFn: () => aiApi.getStatus().then(r => r.data),
  })

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!input.trim() || isLoading) return

    const userMsg: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
      timestamp: new Date(),
    }

    setMessages(prev => [...prev, userMsg])
    setInput('')
    setIsLoading(true)

    try {
      const response = await aiApi.query(input)
      const data = response.data

      const assistantMsg: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: data.response,
        recommendation: data.recommendation,
        sources: data.sources,
        timestamp: new Date(),
      }

      setMessages(prev => [...prev, assistantMsg])
    } catch (error) {
      const errorMsg: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: 'I apologize, but I am unable to process your request at the moment. The AI engine may be temporarily unavailable.',
        timestamp: new Date(),
      }
      setMessages(prev => [...prev, errorMsg])
    } finally {
      setIsLoading(false)
    }
  }

  const getRecIcon = (rec?: string) => {
    switch (rec) {
      case 'DOWNSIZE': return <TrendingDown size={14} />
      case 'SCALE': return <Cpu size={14} />
      case 'REVIEW': return <Shield size={14} />
      case 'REMEDIATE': return <AlertTriangle size={14} />
      default: return <Sparkles size={14} />
    }
  }

  const getRecColor = (rec?: string) => {
    switch (rec) {
      case 'DOWNSIZE': return 'bg-blue-50 text-blue-700'
      case 'SCALE': return 'bg-purple-50 text-purple-700'
      case 'REVIEW': return 'bg-yellow-50 text-yellow-700'
      case 'REMEDIATE': return 'bg-red-50 text-red-700'
      default: return 'bg-gray-50 text-gray-700'
    }
  }

  const suggestions = [
    'What is the cost optimization opportunity for eks-001?',
    'Show me the health status of all clusters',
    'Are there any compliance failures this week?',
    'Identify security vulnerabilities in production',
  ]

  return (
    <div className="space-y-6 h-[calc(100vh-6rem)] flex flex-col">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">AI Infrastructure Assistant</h2>
          <p className="text-gray-500 mt-1">RAG-powered natural language queries</p>
        </div>
        <div className="flex items-center gap-2">
          <div className={`w-2 h-2 rounded-full ${aiStatus?.status === 'OPERATIONAL' ? 'bg-green-500' : 'bg-red-500'}`} />
          <span className="text-sm text-gray-600">{aiStatus?.model || 'AI Engine'}</span>
        </div>
      </div>

      {/* Chat Container */}
      <div className="flex-1 card flex flex-col overflow-hidden p-0">
        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {messages.map((msg) => (
            <div key={msg.id} className={`flex gap-3 ${msg.role === 'user' ? 'flex-row-reverse' : ''}`}>
              <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${
                msg.role === 'user' ? 'bg-primary-100' : 'bg-purple-100'
              }`}>
                {msg.role === 'user' ? <User size={16} className="text-primary-600" /> : <Bot size={16} className="text-purple-600" />}
              </div>
              <div className={`max-w-[80%] ${msg.role === 'user' ? 'items-end' : 'items-start'}`}>
                <div className={`p-4 rounded-2xl ${
                  msg.role === 'user' 
                    ? 'bg-primary-600 text-white rounded-br-none' 
                    : 'bg-gray-100 text-gray-900 rounded-bl-none'
                }`}>
                  <p className="text-sm leading-relaxed">{msg.content}</p>

                  {msg.recommendation && (
                    <div className={`mt-3 inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${getRecColor(msg.recommendation)}`}>
                      {getRecIcon(msg.recommendation)}
                      {msg.recommendation}
                    </div>
                  )}

                  {msg.sources && msg.sources.length > 0 && (
                    <div className="mt-3 flex flex-wrap gap-2">
                      {msg.sources.map((src, i) => (
                        <span key={i} className="text-xs bg-white/50 px-2 py-0.5 rounded">
                          {src.source} ({(src.relevance * 100).toFixed(0)}%)
                        </span>
                      ))}
                    </div>
                  )}
                </div>
                <span className="text-xs text-gray-400 mt-1 px-1">
                  {msg.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
            </div>
          ))}

          {isLoading && (
            <div className="flex gap-3">
              <div className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                <Bot size={16} className="text-purple-600" />
              </div>
              <div className="bg-gray-100 rounded-2xl rounded-bl-none p-4">
                <div className="flex gap-1">
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                </div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Suggestions */}
        {messages.length <= 2 && (
          <div className="px-6 pb-2">
            <p className="text-xs text-gray-500 mb-2">Try asking:</p>
            <div className="flex flex-wrap gap-2">
              {suggestions.map((s, i) => (
                <button
                  key={i}
                  onClick={() => { setInput(s); }}
                  className="text-xs bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1.5 rounded-full transition-colors"
                >
                  {s}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Input */}
        <form onSubmit={handleSubmit} className="p-4 border-t border-gray-200">
          <div className="flex gap-3">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask about your infrastructure..."
              className="flex-1 border border-gray-300 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
            <button
              type="submit"
              disabled={isLoading || !input.trim()}
              className="bg-primary-600 text-white px-4 py-2.5 rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              <Send size={18} />
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
