import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { healthApi } from '../services/api'
import { Server, RefreshCw, Cpu, HardDrive } from 'lucide-react'

export default function Clusters() {
  const [selectedCluster, setSelectedCluster] = useState<string | null>(null)

  const { data: clustersData } = useQuery({
    queryKey: ['clusters-list'],
    queryFn: () => healthApi.getClusterHealth('ke-health-eks-001').then(r => r.data),
  })

  const clusters = clustersData || []

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Cluster Management</h2>
          <p className="text-gray-500 mt-1">Monitor and manage EKS and on-premises clusters</p>
        </div>
        <button 
          onClick={() => window.location.reload()}
          className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
        >
          <RefreshCw size={16} />
          Refresh
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Cluster List */}
        <div className="lg:col-span-1 space-y-4">
          {['ke-health-eks-001', 'on-prem-k8s-002', 'dev-minikube-003'].map((id) => (
            <div 
              key={id}
              onClick={() => setSelectedCluster(id)}
              className={`card cursor-pointer transition-all ${selectedCluster === id ? 'ring-2 ring-primary-500' : ''}`}
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center">
                  <Server className="text-primary-600" size={20} />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{id}</p>
                  <p className="text-xs text-gray-500">EKS us-east-1</p>
                </div>
                <span className="badge badge-healthy text-xs">HEALTHY</span>
              </div>
            </div>
          ))}
        </div>

        {/* Cluster Detail */}
        <div className="lg:col-span-2">
          <div className="card">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              {selectedCluster || 'Select a cluster'}
            </h3>

            {selectedCluster ? (
              <div className="space-y-6">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div className="p-4 bg-gray-50 rounded-lg">
                    <Cpu className="text-gray-400 mb-2" size={20} />
                    <p className="text-2xl font-bold text-gray-900">74.5%</p>
                    <p className="text-xs text-gray-500">CPU Utilization</p>
                  </div>
                  <div className="p-4 bg-gray-50 rounded-lg">
                    <HardDrive className="text-gray-400 mb-2" size={20} />
                    <p className="text-2xl font-bold text-gray-900">82.1%</p>
                    <p className="text-xs text-gray-500">Memory</p>
                  </div>
                  <div className="p-4 bg-gray-50 rounded-lg">
                    <Server className="text-gray-400 mb-2" size={20} />
                    <p className="text-2xl font-bold text-gray-900">12</p>
                    <p className="text-xs text-gray-500">Active Pods</p>
                  </div>
                  <div className="p-4 bg-gray-50 rounded-lg">
                    <RefreshCw className="text-gray-400 mb-2" size={20} />
                    <p className="text-2xl font-bold text-gray-900">5</p>
                    <p className="text-xs text-gray-500">Nodes</p>
                  </div>
                </div>

                <div>
                  <h4 className="text-sm font-medium text-gray-700 mb-2">Recent Events</h4>
                  <div className="space-y-2">
                    {clusters.slice(0, 5).map((event: any, i: number) => (
                      <div key={i} className="flex items-center justify-between p-3 bg-gray-50 rounded-md text-sm">
                        <span className="font-mono text-gray-600">{event.nodeId}</span>
                        <span className={`badge badge-${event.status?.toLowerCase() || 'unknown'} text-xs`}>
                          {event.status}
                        </span>
                        <span className="text-gray-500">{event.activePods} pods</span>
                      </div>
                    ))}
                    {clusters.length === 0 && (
                      <p className="text-sm text-gray-400 py-4 text-center">No events recorded yet</p>
                    )}
                  </div>
                </div>
              </div>
            ) : (
              <div className="flex items-center justify-center h-64 text-gray-400">
                <p>Select a cluster to view details</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
