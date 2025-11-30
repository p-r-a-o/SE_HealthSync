"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function DoctorPatientsPage() {
  const { user } = useAuth()
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const response = await api.get(`/doctors/${user?.userId}/patients`)
        setPatients(response.data)
      } catch (err) {
        setError("Failed to load patients")
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchPatients()
    }
  }, [user?.userId])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">My Patients</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

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
              <div className="space-y-2 text-sm">
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
                  <p className="text-gray-600">Age</p>
                  <p>
                    {patient.dateOfBirth
                      ? new Date().getFullYear() - new Date(patient.dateOfBirth).getFullYear()
                      : "N/A"}
                  </p>
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
