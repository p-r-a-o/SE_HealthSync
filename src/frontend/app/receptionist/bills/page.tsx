"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import api from "@/lib/api"

export default function ReceptionistBillsPage() {
  const [bills, setBills] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [filter, setFilter] = useState("ALL")
  const [selectedBill, setSelectedBill] = useState<any>(null)
  const [paymentAmount, setPaymentAmount] = useState("")

  useEffect(() => {
    const fetchBills = async () => {
      try {
        let response
        if (filter === "UNPAID") {
          response = await api.get("/bills/unpaid")
        } else {
          response = await api.get("/bills")
        }
        setBills(response.data)
      } catch (err) {
        setError("Failed to load bills")
      } finally {
        setLoading(false)
      }
    }

    fetchBills()
  }, [filter])

  const handleProcessPayment = async () => {
    if (!selectedBill || !paymentAmount) {
      setError("Please enter payment amount")
      return
    }

    try {
      await api.post(`/bills/${selectedBill.billId}/payment?amount=${paymentAmount}`)
      const updatedBills = await api.get("/bills")
      setBills(updatedBills.data)
      setSelectedBill(null)
      setPaymentAmount("")
      alert("Payment processed successfully")
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to process payment")
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Billing Management</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="mb-6 flex gap-2">
        {["ALL", "UNPAID", "PAID"].map((status) => (
          <Button
            key={status}
            onClick={() => setFilter(status)}
            className={`${filter === status ? "bg-blue-600" : "bg-gray-400"} hover:bg-blue-700`}
          >
            {status}
          </Button>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          {bills.length === 0 ? (
            <Card className="p-8 text-center">
              <p className="text-gray-600">No bills found</p>
            </Card>
          ) : (
            <div className="space-y-4">
              {bills.map((bill: any) => (
                <Card
                  key={bill.billId}
                  className={`p-6 cursor-pointer transition-all ${
                    selectedBill?.billId === bill.billId ? "ring-2 ring-blue-500" : "hover:shadow-md"
                  }`}
                  onClick={() => setSelectedBill(bill)}
                >
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div>
                      <p className="text-sm text-gray-600">Patient</p>
                      <p className="font-semibold">{bill.patientName}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Total</p>
                      <p className="font-semibold text-lg">₹{bill.totalAmount}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Balance</p>
                      <p className={`font-semibold ${bill.balanceAmount > 0 ? "text-red-600" : "text-green-600"}`}>
                        ₹{bill.balanceAmount}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Status</p>
                      <p className={`font-semibold ${bill.status === "PAID" ? "text-green-600" : "text-orange-600"}`}>
                        {bill.status}
                      </p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>

        {selectedBill && (
          <Card className="p-6 h-fit">
            <h2 className="text-xl font-bold mb-4">Bill Details</h2>

            <div className="space-y-3 mb-4 pb-4 border-b">
              <div>
                <p className="text-sm text-gray-600">Patient</p>
                <p className="font-semibold">{selectedBill.patientName}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Bill ID</p>
                <p className="font-semibold">{selectedBill.billId}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Total Amount</p>
                <p className="text-2xl font-bold text-blue-600">₹{selectedBill.totalAmount}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Paid Amount</p>
                <p className="font-semibold">₹{selectedBill.paidAmount}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Balance</p>
                <p
                  className={`text-2xl font-bold ${selectedBill.balanceAmount > 0 ? "text-red-600" : "text-green-600"}`}
                >
                  ₹{selectedBill.balanceAmount}
                </p>
              </div>
            </div>

            {selectedBill.billItems && selectedBill.billItems.length > 0 && (
              <div className="mb-4 pb-4 border-b">
                <p className="font-semibold mb-2">Items:</p>
                <div className="space-y-1 text-sm">
                  {selectedBill.billItems.map((item: any) => (
                    <div key={item.itemId} className="flex justify-between">
                      <span>{item.description}</span>
                      <span>₹{item.totalPrice}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {selectedBill.balanceAmount > 0 && (
              <div className="space-y-3">
                <div>
                  <label className="block text-sm font-medium mb-1">Payment Amount</label>
                  <input
                    type="number"
                    value={paymentAmount}
                    onChange={(e) => setPaymentAmount(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded"
                    placeholder="Enter amount"
                    max={selectedBill.balanceAmount}
                  />
                </div>
                <Button onClick={handleProcessPayment} className="w-full bg-green-600 hover:bg-green-700">
                  Process Payment
                </Button>
              </div>
            )}
          </Card>
        )}
      </div>
    </div>
  )
}
