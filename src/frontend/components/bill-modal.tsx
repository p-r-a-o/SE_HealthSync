"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"
import { X, Plus, Trash2 } from "lucide-react"

interface BillItem {
  itemId?: string
  description: string
  unitPrice: number
  quantity: number
  totalPrice: number
}

interface BillModalProps {
  bill?: any
  onClose: () => void
  onSave: () => void
}

export default function BillModal({ bill, onClose, onSave }: BillModalProps) {
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [submitting, setSubmitting] = useState(false)

  const [formData, setFormData] = useState({
    patientId: "",
    billDate: new Date().toISOString().split("T")[0],
    status: "PENDING",
    paidAmount: 0,
  })

  const [billItems, setBillItems] = useState<BillItem[]>([
    { description: "", unitPrice: 0, quantity: 1, totalPrice: 0 },
  ])

  useEffect(() => {
    fetchPatients()
    if (bill) {
      setFormData({
        patientId: bill.patientId || "",
        billDate: bill.billDate || new Date().toISOString().split("T")[0],
        status: bill.status || "PENDING",
        paidAmount: bill.paidAmount || 0,
      })
      if (bill.billItems && bill.billItems.length > 0) {
        setBillItems(
          bill.billItems.map((item: any) => ({
            itemId: item.itemId,
            description: item.description,
            unitPrice: item.unitPrice || item.totalPrice / (item.quantity || 1),
            quantity: item.quantity || 1,
            totalPrice: item.totalPrice,
          })),
        )
      }
    }
  }, [bill])

  const fetchPatients = async () => {
    try {
      const response = await api.get("/patients")
      setPatients(response.data || [])
    } catch (err) {
      setError("Failed to load patients")
    } finally {
      setLoading(false)
    }
  }

  const handleFormChange = (e: any) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: name === "paidAmount" ? Number.parseFloat(value) || 0 : value,
    }))
    console.log(value,name,formData)
  }

  const handleItemChange = (index: number, field: string, value: any) => {
    const newItems = [...billItems]
    const item = newItems[index]

    if (field === "unitPrice" || field === "quantity") {
      item[field] = Number.parseFloat(value) || 0
      item.totalPrice = item.unitPrice * item.quantity
    } else {
      item[field] = value
    }

    setBillItems(newItems)
  }

  const addBillItem = () => {
    setBillItems([...billItems, { description: "", unitPrice: 0, quantity: 1, totalPrice: 0 }])
  }

  const removeBillItem = (index: number) => {
    if (billItems.length > 1) {
      setBillItems(billItems.filter((_, i) => i !== index))
    }
  }

  const getTotalAmount = () => {
    return billItems.reduce((sum, item) => sum + item.totalPrice, 0)
  }

  const handleSubmit = async (e: any) => {
    e.preventDefault()

    if (!formData.patientId) {
      setError("Please select a patient")
      return
    }

    if (billItems.some((item) => !item.description || item.totalPrice === 0)) {
      setError("Please fill in all bill items")
      return
    }

    try {
      setSubmitting(true)
      setError("")

      const totalAmount = getTotalAmount()
      const billData = {
        patientId: formData.patientId,
        billDate: formData.billDate,
        totalAmount,
        paidAmount: Math.min(formData.paidAmount, totalAmount),
        status: formData.status,
      }

      if (bill?.billId) {
        // Update existing bill
        await api.put(`/bills/${bill.billId}`, billData)

        // Update bill items
        for (const item of billItems) {
          if (item.itemId) {
            await api.put(`/bills/items/${item.itemId}`, {
              description: item.description,
              quantity: item.quantity,
              unitPrice: item.unitPrice,
              totalPrice: item.totalPrice,
            })
          } else {
            await api.post("/bills/items", {
              billId: bill.billId,
              description: item.description,
              quantity: item.quantity,
              unitPrice: item.unitPrice,
              totalPrice: item.totalPrice,
            })
          }
        }
      } else {
        // Create new bill
        const billResponse = await api.post("/bills/with-items", {
          bill: billData,
          items: billItems.map((item) => ({
            description: item.description,
            quantity: item.quantity,
            unitPrice: item.unitPrice,
            totalPrice: item.totalPrice,
            paidAmount: item.paidAmount,
          })),
        })
      }

      onSave()
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to save bill")
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <Card className="w-full max-w-2xl max-h-[90vh] overflow-y-auto bg-white">
        <div className="sticky top-0 bg-white border-b p-6 flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-900">{bill ? "Edit Bill" : "Create New Bill"}</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && <div className="p-4 bg-red-50 border border-red-200 text-red-700 rounded">{error}</div>}

          {/* Patient Selection */}
          <div>
            <label className="block text-sm font-semibold text-gray-900 mb-2">Patient *</label>
            <select
              name="patientId"
              value={formData.patientId}
              onChange={handleFormChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">Select a patient...</option>
              {patients.map((patient: any) => (
                <option key={patient.personId} value={patient.personId}>
                  {patient.firstName} {patient.lastName} ({patient.personId})
                </option>
              ))}
            </select>
          </div>

          {/* Bill Date and Status */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold text-gray-900 mb-2">Bill Date</label>
              <input
                type="date"
                name="billDate"
                value={formData.billDate}
                onChange={handleFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-900 mb-2">Status</label>
              <select
                name="status"
                value={formData.status}
                onChange={handleFormChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="PENDING">Pending</option>
                <option value="PAID">Paid</option>
              </select>
            </div>
          </div>

          {/* Bill Items */}
          <div>
            <div className="flex justify-between items-center mb-4">
              <label className="block text-sm font-semibold text-gray-900">Bill Items *</label>
              <Button
                type="button"
                onClick={addBillItem}
                className="bg-blue-600 hover:bg-blue-700 text-white text-sm flex items-center gap-1"
              >
                <Plus className="w-4 h-4" />
                Add Item
              </Button>
            </div>

            <div className="space-y-3 max-h-96 overflow-y-auto">
              {billItems.map((item, index) => (
                <Card key={index} className="p-4 bg-gray-50 border border-gray-200">
                  <div className="grid grid-cols-12 gap-2 items-start">
                    <input
                      type="text"
                      placeholder="Description (e.g., Consultation, Medication)"
                      value={item.description}
                      onChange={(e) => handleItemChange(index, "description", e.target.value)}
                      className="col-span-5 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <input
                      type="number"
                      placeholder="Unit Price"
                      value={item.unitPrice || ""}
                      onChange={(e) => handleItemChange(index, "unitPrice", e.target.value)}
                      step="0.01"
                      className="col-span-2 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <input
                      type="number"
                      placeholder="Qty"
                      value={item.quantity || ""}
                      onChange={(e) => handleItemChange(index, "quantity", e.target.value)}
                      min="1"
                      className="col-span-1 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                    <div className="col-span-3 px-3 py-2 bg-white border border-gray-300 rounded-lg text-sm font-semibold text-gray-900">
                      ₹{item.totalPrice?.toFixed(2)}
                    </div>
                    <button
                      type="button"
                      onClick={() => removeBillItem(index)}
                      disabled={billItems.length === 1}
                      className="col-span-1 p-2 text-red-600 hover:bg-red-50 rounded disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </Card>
              ))}
            </div>
          </div>

          {/* Total and Paid Amount */}
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-4 rounded-lg border border-blue-200 space-y-3">
            <div className="flex justify-between">
              <span className="font-semibold text-gray-900">Total Amount:</span>
              <span className="font-bold text-lg text-blue-600">₹{getTotalAmount().toFixed(2)}</span>
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-900 mb-2">Amount Paid</label>
              <input
                type="number"
                name="paidAmount"
                value={formData.paidAmount || ""}
                onChange={handleFormChange}
                step="0.01"
                min="0"
                max={getTotalAmount()}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div className="flex justify-between pt-2 border-t border-blue-200">
              <span className="font-semibold text-gray-900">Balance:</span>
              <span
                className={`font-bold text-lg ${getTotalAmount() - formData.paidAmount > 0 ? "text-red-600" : "text-green-600"}`}
              >
                ₹{(getTotalAmount() - formData.paidAmount).toFixed(2)}
              </span>
            </div>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 justify-end pt-4 border-t">
            <Button
              type="button"
              onClick={onClose}
              className="px-6 py-2 border border-gray-300 text-gray-900 hover:bg-gray-50 rounded-lg"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={submitting}
              className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg disabled:opacity-50"
            >
              {submitting ? "Saving..." : bill ? "Update Bill" : "Create Bill"}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  )
}
