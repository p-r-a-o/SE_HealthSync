"use client";

import type React from "react";

import { useState, useEffect } from "react";
import { useAuth } from "@/lib/auth-context";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import api from "@/lib/api";

export default function DoctorProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState<any>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<any>({});
  const [departments, setDepartments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // -------------------- Fetch doctor + departments --------------------
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [profileRes, deptRes] = await Promise.all([
          api.get(`/doctors/${user?.userId}`),
          api.get(`/departments`),
        ]);

        setProfile(profileRes.data);
        setFormData(profileRes.data);
        setDepartments(deptRes.data);
      } catch (err) {
        setError("Failed to load profile or departments");
      } finally {
        setLoading(false);
      }
    };

    if (user?.userId) fetchData();
  }, [user?.userId]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;

    // If department select changed → update both id + name
    if (name === "departmentId") {
      const dept = departments.find((d) => d.departmentId === value);
      setFormData((prev: any) => ({
        ...prev,
        departmentId: value,
        departmentName: dept?.departmentName || "",
      }));
      return;
    }

    setFormData((prev: any) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      await api.put(`/doctors/${user?.userId}`, formData);
      setProfile(formData);
      setIsEditing(false);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to update profile");
    }
  };

  if (loading) return <div className="container mx-auto p-6">Loading...</div>;

  return (
    <div className="container mx-auto p-6">
      <Card className="max-w-2xl mx-auto">
        <div className="p-8">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold">Doctor Profile</h1>
            <Button
              onClick={() => setIsEditing(!isEditing)}
              className="bg-blue-600 hover:bg-blue-700"
            >
              {isEditing ? "Cancel" : "Edit"}
            </Button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>
          )}

          {/* -------------------- VIEW MODE -------------------- */}
          {!isEditing ? (
            <div className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600">First Name</p>
                  <p className="text-lg font-semibold">{profile?.firstName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Last Name</p>
                  <p className="text-lg font-semibold">{profile?.lastName}</p>
                </div>
              </div>

              <div>
                <p className="text-sm text-gray-600">Email</p>
                <p className="text-lg font-semibold">{profile?.email}</p>
              </div>

              <div>
                <p className="text-sm text-gray-600">Contact Number</p>
                <p className="text-lg font-semibold">{profile?.contactNumber}</p>
              </div>

              <div>
                <p className="text-sm text-gray-600">Specialization</p>
                <p className="text-lg font-semibold">{profile?.specialization}</p>
              </div>

              <div>
                <p className="text-sm text-gray-600">Qualification</p>
                <p className="text-lg font-semibold">{profile?.qualification}</p>
              </div>

              <div>
                <p className="text-sm text-gray-600">Consultation Fee</p>
                <p className="text-lg font-semibold">₹{profile?.consultationFee}</p>
              </div>

              <div>
                <p className="text-sm text-gray-600">Department</p>
                <p className="text-lg font-semibold">
                  {profile?.departmentName} ({profile?.departmentId})
                </p>
              </div>
            </div>
          ) : (
            /* -------------------- EDIT MODE -------------------- */
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">
                    First Name
                  </label>
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">
                    Last Name
                  </label>
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Contact Number
                </label>
                <input
                  type="tel"
                  name="contactNumber"
                  value={formData.contactNumber}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Specialization
                </label>
                <input
                  type="text"
                  name="specialization"
                  value={formData.specialization}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Qualification
                </label>
                <input
                  type="text"
                  name="qualification"
                  value={formData.qualification}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Consultation Fee (₹)
                </label>
                <input
                  type="number"
                  name="consultationFee"
                  value={formData.consultationFee}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                />
              </div>

              {/* -------------------- Department Dropdown -------------------- */}
              <div>
                <label className="block text-sm font-medium mb-1">
                  Department
                </label>

                <select
                  name="departmentId"
                  value={formData.departmentId}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                >
                  <option value="">Select Department...</option>

                  {departments.map((dept) => (
                    <option key={dept.departmentId} value={dept.departmentId}>
                      {dept.name}
                    </option>
                  ))}
                </select>
              </div>

              <Button
                type="submit"
                className="w-full bg-green-600 hover:bg-green-700"
              >
                Save Changes
              </Button>
            </form>
          )}
        </div>
      </Card>
    </div>
  );
}
