"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import api from "@/lib/api"

export default function ReceptionistBedsPage() {
  const [beds, setBeds] = useState([])
  const [departments, setDepartments] = useState([])
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [filter, setFilter] = useState("ALL")

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [bedsRes, depsRes, patientsRes] = await Promise.all([
          api.get("/beds"),
          api.get("/departments"),
          api.get("/patients"),
        ])
        setBeds(bedsRes.data)
        setDepartments(depsRes.data)
        setPatients(patientsRes.data)
      } catch (err) {
        setError("Failed to load data")
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  const handleAssignBed = async (bedId: string) => {
    const patientId = prompt("Enter patient ID:")
    if (!patientId) return

    try {
      await api.post(`/beds/${bedId}/assign?patientId=${patientId}`)
      const updatedBeds = await api.get("/beds")
      setBeds(updatedBeds.data)
      alert("Bed assigned successfully")
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to assign bed")
    }
  }

  const handleReleaseBed = async (bedId: string) => {
    try {
      await api.post(`/beds/${bedId}/release`)
      const updatedBeds = await api.get("/beds")
      setBeds(updatedBeds.data)
      alert("Bed released successfully")
    } catch (err) {
      setError("Failed to release bed")
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  const filteredBeds = beds.filter((bed) => {
    if (filter === "AVAILABLE") return !bed.isOccupied
    if (filter === "OCCUPIED") return bed.isOccupied
    return true
  })

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Bed Management</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="mb-6 flex gap-2">
        {["ALL", "AVAILABLE", "OCCUPIED"].map((status) => (
          <Button
            key={status}
            onClick={() => setFilter(status)}
            className={`${filter === status ? "bg-blue-600" : "bg-gray-400"} hover:bg-blue-700`}
          >
            {status}
          </Button>
        ))}
      </div>

      {filteredBeds.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No beds found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredBeds.map((bed: any) => (
            <Card key={bed.bedId} className={`p-6 ${bed.isOccupied ? "bg-red-50" : "bg-green-50"}`}>
              <div className="mb-4">
                <p className="text-sm text-gray-600">Bed ID</p>
                <p className="font-semibold text-lg">{bed.bedId}</p>
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <p className="text-sm text-gray-600">Department</p>
                  <p className="font-semibold">{bed.departmentName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  <p className={`font-semibold ${bed.isOccupied ? "text-red-600" : "text-green-600"}`}>
                    {bed.isOccupied ? "Occupied" : "Available"}
                  </p>
                </div>
              </div>

              {bed.isOccupied && (
                <div className="mb-4 p-3 bg-gray-100 rounded">
                  <p className="text-sm text-gray-600">Patient</p>
                  <p className="font-semibold">{bed.patientName}</p>
                </div>
              )}

              <div className="mb-4">
                <p className="text-sm text-gray-600">Daily Rate</p>
                <p className="font-semibold">₹{bed.dailyRate}</p>
              </div>

              <div className="flex gap-2">
                {!bed.isOccupied ? (
                  <Button onClick={() => handleAssignBed(bed.bedId)} className="flex-1 bg-blue-600 hover:bg-blue-700">
                    Assign
                  </Button>
                ) : (
                  <Button
                    onClick={() => handleReleaseBed(bed.bedId)}
                    className="flex-1 bg-orange-600 hover:bg-orange-700"
                  >
                    Release
                  </Button>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
