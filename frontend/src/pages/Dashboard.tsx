import { useQuery } from '@tanstack/react-query'
import { dashboardApi } from '../services/api'
import { Activity, AlertTriangle, CheckCircle, Clock, Server, Shield } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

export default function Dashboard() {
  const { data: summary, isLoading: summaryLoading } = useQuery({
    queryKey: ['dashboard-summary'],
    queryFn: () => dashboardApi.getSummary().then(r => r.data),
    refetchInterval: 30000,
  })

  const { data: compliance } = useQuery({
    queryKey: ['compliance'],
    queryFn: () => dashboardApi.getCompliance().then(r => r.data),
    refetchInterval: 60000,
  })

  const { data: alerts } = useQuery({
    queryKey: ['alerts'],
    queryFn: () => dashboardApi.getAlerts().then(r => r.data),
    refetchInterval: 30000,
  })

  if (summaryLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
      </div>
    )
  }

  const clusters = summary?.clusters || []
  const criticalCount = alerts?.critical?.length || 0
  const failedCount = alerts?.failedCompliance?.length || 0

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Dashboard</h2>
        <p className="text-gray-500 mt-1">Real-time infrastructure observability across all clusters</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Total Clusters</p>
              <p className="text-2xl font-bold text-gray-900">{summary?.totalClusters || 0}</p>
            </div>
            <div className="w-10 h-10 bg-blue-50 rounded-lg flex items-center justify-center">
              <Server className="text-blue-600" size={20} />
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Critical Alerts</p>
              <p className={`text-2xl font-bold ${criticalCount > 0 ? 'text-red-600' : 'text-gray-900'}`}>
                {criticalCount}
              </p>
            </div>
            <div className="w-10 h-10 bg-red-50 rounded-lg flex items-center justify-center">
              <AlertTriangle className="text-red-600" size={20} />
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Compliance Rate</p>
              <p className="text-2xl font-bold text-gray-900">
                {compliance ? `${compliance.passRate?.toFixed(1)}%` : '—'}
              </p>
            </div>
            <div className="w-10 h-10 bg-green-50 rounded-lg flex items-center justify-center">
              <Shield className="text-green-600" size={20} />
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Failed Audits</p>
              <p className={`text-2xl font-bold ${failedCount > 0 ? 'text-orange-600' : 'text-gray-900'}`}>
                {failedCount}
              </p>
            </div>
            <div className="w-10 h-10 bg-orange-50 rounded-lg flex items-center justify-center">
              <Activity className="text-orange-600" size={20} />
            </div>
          </div>
        </div>
      </div>

      {/* Clusters Overview */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Cluster Health Overview</h3>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200">
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Cluster ID</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Status</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Active Pods</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">CPU</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Memory</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-500">Last Updated</th>
              </tr>
            </thead>
            <tbody>
              {clusters.map((cluster: any) => (
                <tr key={cluster.clusterId} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 font-mono text-sm">{cluster.clusterId}</td>
                  <td className="py-3 px-4">
                    <span className={`badge badge-${cluster.status?.toLowerCase() || 'unknown'}`}>
                      {cluster.status || 'UNKNOWN'}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-sm">{cluster.activePods || 0}</td>
                  <td className="py-3 px-4 text-sm">{cluster.metrics?.cpu_utilization || '—'}%</td>
                  <td className="py-3 px-4 text-sm">{cluster.metrics?.memory_utilization || '—'}%</td>
                  <td className="py-3 px-4 text-sm text-gray-500">
                    {cluster.lastUpdated ? formatDistanceToNow(new Date(cluster.lastUpdated), { addSuffix: true }) : '—'}
                  </td>
                </tr>
              ))}
              {clusters.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-gray-400">
                    No cluster data available. Start the telemetry generator.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Compliance & Alerts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Compliance Summary (30d)</h3>
          {compliance && (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Passed</span>
                <span className="text-sm font-medium text-green-600">{compliance.passed}</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-green-500 h-2 rounded-full" 
                  style={{ width: `${(compliance.passed / (compliance.total || 1)) * 100}%` }}
                />
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Failed</span>
                <span className="text-sm font-medium text-red-600">{compliance.failed}</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-red-500 h-2 rounded-full" 
                  style={{ width: `${(compliance.failed / (compliance.total || 1)) * 100}%` }}
                />
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Pending Review</span>
                <span className="text-sm font-medium text-yellow-600">{compliance.pendingReview}</span>
              </div>
            </div>
          )}
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Alerts</h3>
          <div className="space-y-3">
            {alerts?.critical?.length === 0 && alerts?.failedCompliance?.length === 0 ? (
              <div className="flex items-center gap-3 text-green-600">
                <CheckCircle size={18} />
                <span className="text-sm">All systems nominal. No active alerts.</span>
              </div>
            ) : (
              <>
                {alerts?.critical?.map((alert: any, i: number) => (
                  <div key={i} className="flex items-start gap-3 p-3 bg-red-50 rounded-md">
                    <AlertTriangle className="text-red-600 shrink-0 mt-0.5" size={16} />
                    <div>
                      <p className="text-sm font-medium text-red-800">
                        Critical: {alert.clusterId} — {alert.nodeId}
                      </p>
                      <p className="text-xs text-red-600 mt-1">
                        {alert.status} • CPU: {alert.metrics?.cpu_utilization}%
                      </p>
                    </div>
                  </div>
                ))}
                {alerts?.failedCompliance?.map((audit: any, i: number) => (
                  <div key={`audit-${i}`} className="flex items-start gap-3 p-3 bg-orange-50 rounded-md">
                    <Clock className="text-orange-600 shrink-0 mt-0.5" size={16} />
                    <div>
                      <p className="text-sm font-medium text-orange-800">
                        Compliance: {audit.eventId}
                      </p>
                      <p className="text-xs text-orange-600 mt-1">
                        {audit.action} on {audit.resource} by {audit.user}
                      </p>
                    </div>
                  </div>
                ))}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
