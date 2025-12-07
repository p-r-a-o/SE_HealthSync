"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"
import { CheckCircle } from "lucide-react"

interface PrescriptionItem {
  prescriptionItemId: string
  medicationName: string
  quantity: number
  dosage: string
}

interface Prescription {
  prescriptionId: string
  patientId: string
  patientName: string
  doctorId: string
  doctorName: string
  dateIssued: string
  status: string
  instructions: string
  prescriptionItems: PrescriptionItem[]
}

export default function PharmacistPrescriptionsPage() {
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [filter, setFilter] = useState("PENDING")
  const [dispensing, setDispensing] = useState<string | null>(null)

  useEffect(() => {
    fetchPrescriptions()
  }, [filter])

  const fetchPrescriptions = async () => {
    try {
      setLoading(true)
      const response = await api.get(`/prescriptions/status/${filter}`)
      setPrescriptions(response.data)
      setError("")
    } catch (err) {
      setError("Failed to load prescriptions")
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleDispenseMedication = async (prescriptionId: string) => {
    if (!window.confirm("Mark this prescription as dispensed?")) return

    try {
      setDispensing(prescriptionId)
      await api.post(`/medications/dispense/${prescriptionId}`)
      setSuccess("Medication dispensed successfully")
      fetchPrescriptions()
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to dispense medication")
      console.error(err)
    } finally {
      setDispensing(null)
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold mb-2">Prescriptions</h1>
        <p className="text-gray-600">Manage and dispense medications</p>
      </div>

      {/* Alerts */}
      {error && (
        <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-lg flex justify-between items-center">
          <span>{error}</span>
          <button onClick={() => setError("")} className="text-xl">
            ×
          </button>
        </div>
      )}
      {success && (
        <div className="mb-4 p-4 bg-green-100 text-green-700 rounded-lg flex justify-between items-center">
          <span>{success}</span>
          <button onClick={() => setSuccess("")} className="text-xl">
            ×
          </button>
        </div>
      )}

      {/* Status Filter */}
      <div className="mb-6 flex gap-3 flex-wrap">
        {["PENDING", "DISPENSED", "CANCELLED"].map((status) => (
          <Button
            key={status}
            onClick={() => setFilter(status)}
            className={`${
              filter === status
                ? "bg-blue-600 text-white hover:bg-blue-700"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            {status}
          </Button>
        ))}
      </div>

      {/* Prescriptions List */}
      {prescriptions.length === 0 ? (
        <Card className="p-12 text-center">
          <p className="text-gray-600 text-lg">No {filter.toLowerCase()} prescriptions found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-6">
          {prescriptions.map((rx) => (
            <Card key={rx.prescriptionId} className="p-6 hover:shadow-lg transition-shadow">
              {/* Prescription Header */}
              <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-6 pb-6 border-b">
                <div>
                  <p className="text-sm text-gray-600">Patient</p>
                  <p className="font-semibold text-blue-600">{rx.patientName}</p>
                  <p className="text-xs text-gray-500">{rx.patientId}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Doctor</p>
                  <p className="font-semibold">{rx.doctorName}</p>
                  <p className="text-xs text-gray-500">{rx.doctorId}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Date Issued</p>
                  <p className="font-semibold">{new Date(rx.dateIssued).toLocaleDateString()}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Prescription ID</p>
                  <p className="font-mono text-xs font-semibold">{rx.prescriptionId.substring(0, 8)}...</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  <p
                    className={`font-semibold text-sm ${
                      rx.status === "DISPENSED"
                        ? "text-green-600"
                        : rx.status === "CANCELLED"
                          ? "text-red-600"
                          : "text-blue-600"
                    }`}
                  >
                    {rx.status}
                  </p>
                </div>
              </div>

              {/* Instructions */}
              {rx.instructions && (
                <div className="mb-4 p-3 bg-blue-50 rounded border border-blue-200">
                  <p className="text-sm text-gray-600">Instructions</p>
                  <p className="text-sm font-semibold">{rx.instructions}</p>
                </div>
              )}

              {/* Medications */}
              {rx.prescriptionItems && rx.prescriptionItems.length > 0 && (
                <div className="mb-6">
                  <p className="font-bold mb-3 text-lg">Medications:</p>
                  <div className="space-y-2">
                    {rx.prescriptionItems.map((item) => (
                      <div key={item.prescriptionItemId} className="p-3 bg-gray-50 rounded border">
                        <div className="flex justify-between items-start">
                          <div>
                            <p className="font-semibold text-blue-600">{item.medicationName}</p>
                            <p className="text-sm text-gray-600">
                              Quantity: <span className="font-semibold">{item.quantity}</span>
                            </p>
                            {item.dosage && (
                              <p className="text-sm text-gray-600">
                                Dosage: <span className="font-semibold">{item.dosage}</span>
                              </p>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Action Button */}
              {rx.status === "PENDING" && (
                <div className="flex gap-2">
                  <Button
                    onClick={() => handleDispenseMedication(rx.prescriptionId)}
                    disabled={dispensing === rx.prescriptionId}
                    className="flex-1 bg-green-600 hover:bg-green-700 flex gap-2 items-center justify-center"
                  >
                    <CheckCircle size={18} />
                    {dispensing === rx.prescriptionId ? "Dispensing..." : "Dispense Medication"}
                  </Button>
                </div>
              )}
              {rx.status === "DISPENSED" && (
                <div className="p-3 bg-green-50 rounded border border-green-200 text-center">
                  <p className="text-green-700 font-semibold">Medication Dispensed</p>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
