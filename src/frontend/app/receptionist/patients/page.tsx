"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function ReceptionistPatientsPage() {
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const response = await api.get("/patients")
        setPatients(response.data)
      } catch (err) {
        setError("Failed to load patients")
      } finally {
        setLoading(false)
      }
    }

    fetchPatients()
  }, [])

  const handleSearch = async (query: string) => {
    setSearchQuery(query)
    if (!query) {
      const response = await api.get("/patients")
      setPatients(response.data)
    } else {
      try {
        const response = await api.get("/patients/search?name=" + query)
        setPatients(response.data)
      } catch (err) {
        setError("Search failed")
      }
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Patient Management</h1>
      </div>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="mb-6">
        <input
          type="text"
          placeholder="Search patients by name..."
          value={searchQuery}
          onChange={(e) => handleSearch(e.target.value)}
          className="w-full px-4 py-2 border border-gray-300 rounded"
        />
      </div>

      {patients.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No patients found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {patients.map((patient: any) => (
            <Card key={patient.personId} className="p-6">
              <h3 className="text-lg font-semibold mb-4">
                {patient.firstName} {patient.lastName}
              </h3>
              <div className="space-y-2 text-sm mb-4">
                <div>
                  <p className="text-gray-600">Email</p>
                  <p>{patient.email}</p>
                </div>
                <div>
                  <p className="text-gray-600">Contact</p>
                  <p>{patient.contactNumber}</p>
                </div>
                <div>
                  <p className="text-gray-600">Blood Group</p>
                  <p>{patient.bloodGroup}</p>
                </div>
                <div>
                  <p className="text-gray-600">City</p>
                  <p>{patient.city}</p>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
