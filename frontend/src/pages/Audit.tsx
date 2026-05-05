import { useQuery } from '@tanstack/react-query'
import { auditApi } from '../services/api'
import { Shield, CheckCircle, XCircle, Clock, Filter } from 'lucide-react'
import { useState } from 'react'
import { format } from 'date-fns'

export default function Audit() {
  const [days, setDays] = useState(30)

  const { data: dashboard } = useQuery({
    queryKey: ['audit-dashboard'],
    queryFn: () => auditApi.getDashboard().then(r => r.data),
    refetchInterval: 60000,
  })

  const { data: events } = useQuery({
    queryKey: ['audit-events', days],
    queryFn: () => auditApi.getEvents(days).then(r => r.data),
  })

  const { data: breakdown } = useQuery({
    queryKey: ['audit-breakdown', days],
    queryFn: () => auditApi.getBreakdown(days).then(r => r.data),
  })

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Audit & Compliance</h2>
          <p className="text-gray-500 mt-1">CISA-aligned infrastructure audit trail</p>
        </div>
        <div className="flex items-center gap-2">
          <Filter size={16} className="text-gray-400" />
          <select 
            value={days} 
            onChange={(e) => setDays(Number(e.target.value))}
            className="border border-gray-300 rounded-md px-3 py-1.5 text-sm"
          >
            <option value={7}>Last 7 days</option>
            <option value={30}>Last 30 days</option>
            <option value={90}>Last 90 days</option>
          </select>
        </div>
      </div>

      {/* Compliance Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-green-50 rounded-lg flex items-center justify-center">
              <CheckCircle className="text-green-600" size={20} />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{dashboard?.passed || 0}</p>
              <p className="text-xs text-gray-500">Passed</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-red-50 rounded-lg flex items-center justify-center">
              <XCircle className="text-red-600" size={20} />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{dashboard?.failed || 0}</p>
              <p className="text-xs text-gray-500">Failed</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-yellow-50 rounded-lg flex items-center justify-center">
              <Clock className="text-yellow-600" size={20} />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{dashboard?.pendingReview || 0}</p>
              <p className="text-xs text-gray-500">Pending</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-50 rounded-lg flex items-center justify-center">
              <Shield className="text-blue-600" size={20} />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">
                {dashboard?.passRate?.toFixed(1) || 0}%
              </p>
              <p className="text-xs text-gray-500">Pass Rate</p>
            </div>
          </div>
        </div>
      </div>

      {/* Action Breakdown */}
      {breakdown && Object.keys(breakdown).length > 0 && (
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Action Breakdown</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {Object.entries(breakdown).map(([action, count]) => (
              <div key={action} className="p-4 bg-gray-50 rounded-lg">
                <p className="text-sm font-medium text-gray-700">{action}</p>
                <p className="text-2xl font-bold text-gray-900">{count}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Audit Events Table */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Audit Events</h3>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200">
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Event ID</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">User</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Action</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Resource</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Status</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Timestamp</th>
              </tr>
            </thead>
            <tbody>
              {events?.content?.map((event: any) => (
                <tr key={event.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 font-mono text-xs">{event.eventId}</td>
                  <td className="py-3 px-4 text-sm">{event.user}</td>
                  <td className="py-3 px-4">
                    <span className="text-xs font-medium bg-gray-100 text-gray-700 px-2 py-1 rounded">
                      {event.action}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-sm text-gray-600">{event.resource}</td>
                  <td className="py-3 px-4">
                    <span className={`badge badge-${event.complianceStatus?.toLowerCase() || 'unknown'} text-xs`}>
                      {event.complianceStatus}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-sm text-gray-500">
                    {event.timestamp ? format(new Date(event.timestamp), 'MMM d, HH:mm') : '—'}
                  </td>
                </tr>
              ))}
              {(!events?.content || events.content.length === 0) && (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-gray-400">
                    No audit events found. Events will appear as they are generated.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
