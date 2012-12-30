strComputer = "."
Set objWMIService = GetObject("winmgmts:\\" & strComputer & "\root\cimv2")
Set colVolumes = objWMIService.ExecQuery("Select * from Win32_DiskDrive")
For Each objVolume in colVolumes
	Wscript.Echo "Availability: " & objVolume.Availability
	Wscript.Echo "BytesPerSector: " & objVolume.BytesPerSector
	Wscript.Echo "Caption: " & objVolume.Caption
	Wscript.Echo "CompressionMethod: " & objVolume.CompressionMethod
	Wscript.Echo "ConfigManagerErrorCode: " & objVolume.ConfigManagerErrorCode
	Wscript.Echo "ConfigManagerUserConfig: " & objVolume.ConfigManagerUserConfig
	Wscript.Echo "CreationClassName: " & objVolume.CreationClassName
	Wscript.Echo "DefaultBlockSize: " & objVolume.DefaultBlockSize
	Wscript.Echo "Description: " & objVolume.Description
	Wscript.Echo "DeviceID: " & objVolume.DeviceID
	Wscript.Echo "ErrorCleared: " & objVolume.ErrorCleared
	Wscript.Echo "ErrorDescription: " & objVolume.ErrorDescription
	Wscript.Echo "ErrorMethodology: " & objVolume.ErrorMethodology
	Wscript.Echo "Index: " & objVolume.Index
	Wscript.Echo "InstallDate: " & objVolume.InstallDate
	Wscript.Echo "InterfaceType: " & objVolume.InterfaceType
	Wscript.Echo "LastErrorCode: " & objVolume.LastErrorCode
	Wscript.Echo "Manufacturer: " & objVolume.Manufacturer
	Wscript.Echo "MaxBlockSize: " & objVolume.MaxBlockSize
	Wscript.Echo "MaxMediaSize: " & objVolume.MaxMediaSize
	Wscript.Echo "MediaLoaded: " & objVolume.MediaLoaded
	Wscript.Echo "MediaType: " & objVolume.MediaType
	Wscript.Echo "MinBlockSize: " & objVolume.MinBlockSize
	Wscript.Echo "Model: " & objVolume.Model
	Wscript.Echo "Name: " & objVolume.Name
	Wscript.Echo "NeedsCleaning: " & objVolume.NeedsCleaning
	Wscript.Echo "NumberOfMediaSupported: " & objVolume.NumberOfMediaSupported
	Wscript.Echo "Partitions: " & objVolume.Partitions
	Wscript.Echo "PNPDeviceID: " & objVolume.PNPDeviceID
	Wscript.Echo "PowerManagementSupported: " & objVolume.PowerManagementSupported
	Wscript.Echo "SCSIBus: " & objVolume.SCSIBus
	Wscript.Echo "SCSILogicalUnit: " & objVolume.SCSILogicalUnit
	Wscript.Echo "SCSIPort: " & objVolume.SCSIPort
	Wscript.Echo "SCSITargetId: " & objVolume.SCSITargetId
	Wscript.Echo "SectorsPerTrack: " & objVolume.SectorsPerTrack
	Wscript.Echo "Signature: " & objVolume.Signature
	Wscript.Echo "Size: " & objVolume.Size
	Wscript.Echo "Status: " & objVolume.Status
	Wscript.Echo "StatusInfo: " & objVolume.StatusInfo
	Wscript.Echo "SystemCreationClassName: " & objVolume.SystemCreationClassName
	Wscript.Echo "SystemName: " & objVolume.SystemName
	Wscript.Echo "TotalCylinders: " & objVolume.TotalCylinders
	Wscript.Echo "TotalHeads: " & objVolume.TotalHeads
	Wscript.Echo "TotalSectors: " & objVolume.TotalSectors
	Wscript.Echo "TotalTracks: " & objVolume.TotalTracks
	Wscript.Echo "TracksPerCylinder: " & objVolume.TracksPerCylinder
	Wscript.Echo
Next
