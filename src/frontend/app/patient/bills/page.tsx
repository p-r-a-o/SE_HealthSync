"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function PatientBillsPage() {
  const { user } = useAuth()
  const [bills, setBills] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchBills = async () => {
      try {
        const response = await api.get(`/bills/patient/${user?.userId}`)
        setBills(response.data)
      } catch (err) {
        setError("Failed to load bills")
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchBills()
    }
  }, [user?.userId])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">My Bills</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      {bills.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No bills found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-6">
          {bills.map((bill: any) => (
            <Card key={bill.billId} className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                <div>
                  <p className="text-sm text-gray-600">Bill ID</p>
                  <p className="font-semibold">{bill.billId}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Date</p>
                  <p className="font-semibold">{bill.billDate}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  <p className={`font-semibold ${bill.status === "PAID" ? "text-green-600" : "text-orange-600"}`}>
                    {bill.status}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Total Amount</p>
                  <p className="font-semibold text-lg">₹{bill.totalAmount}</p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-4 border-t">
                <div>
                  <p className="text-sm text-gray-600">Paid Amount</p>
                  <p className="font-semibold">₹{bill.paidAmount}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Balance</p>
                  <p className={`font-semibold ${bill.balanceAmount > 0 ? "text-red-600" : "text-green-600"}`}>
                    ₹{bill.balanceAmount}
                  </p>
                </div>
              </div>

              {bill.billItems && bill.billItems.length > 0 && (
                <div className="mt-4 pt-4 border-t">
                  <p className="font-semibold mb-2">Items:</p>
                  <div className="space-y-2 text-sm">
                    {bill.billItems.map((item: any) => (
                      <div key={item.itemId} className="flex justify-between">
                        <span>
                          {item.description} (x{item.quantity})
                        </span>
                        <span>₹{item.totalPrice}</span>
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
