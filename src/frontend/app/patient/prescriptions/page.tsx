"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function PatientPrescriptionsPage() {
  const { user } = useAuth()
  const [prescriptions, setPrescriptions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchPrescriptions = async () => {
      try {
        const response = await api.get(`/prescriptions/patient/${user?.userId}`)
        setPrescriptions(response.data)
      } catch (err) {
        setError("Failed to load prescriptions")
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchPrescriptions()
    }
  }, [user?.userId])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">My Prescriptions</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      {prescriptions.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No prescriptions found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-6">
          {prescriptions.map((rx: any) => (
            <Card key={rx.prescriptionId} className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                <div>
                  <p className="text-sm text-gray-600">Doctor</p>
                  <p className="font-semibold">{rx.doctorName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Date Issued</p>
                  <p className="font-semibold">{rx.dateIssued}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  <p className={`font-semibold ${rx.status === "DISPENSED" ? "text-green-600" : "text-blue-600"}`}>
                    {rx.status}
                  </p>
                </div>
              </div>

              {rx.instructions && (
                <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded">
                  <p className="text-sm">Instructions: {rx.instructions}</p>
                </div>
              )}

              {rx.prescriptionItems && rx.prescriptionItems.length > 0 && (
                <div className="pt-4 border-t">
                  <p className="font-semibold mb-3">Medications:</p>
                  <div className="space-y-2">
                    {rx.prescriptionItems.map((item: any) => (
                      <div key={item.prescriptionItemId} className="p-3 bg-gray-50 rounded">
                        <p className="font-semibold">{item.medicationName}</p>
                        <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                        <p className="text-sm text-gray-600">Price: ₹{item.unitPrice} each</p>
                        <p className="text-sm font-semibold text-blue-600">Total: ₹{item.totalPrice}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
