import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import Clusters from './pages/Clusters'
import Audit from './pages/Audit'
import AIChat from './pages/AIChat'

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/clusters" element={<Clusters />} />
        <Route path="/audit" element={<Audit />} />
        <Route path="/ai" element={<AIChat />} />
      </Routes>
    </Layout>
  )
}

export default App
